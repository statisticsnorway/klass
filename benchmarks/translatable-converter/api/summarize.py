#!/usr/bin/env python3
"""Validate comparable fixtures/responses and summarize local HTTP experiments."""

import argparse
import gzip
import json
import math
from pathlib import Path
import statistics


WORKLOADS = ("labels-100", "labels-1000", "notes-1000")


def load(directory):
    results = []
    for path in sorted(directory.glob("*.json.gz")):
        with gzip.open(path, "rt") as stream:
            row = json.load(stream)
        row["path"] = path.name
        results.append(row)
    if not results:
        raise SystemExit(f"No results in {directory}")
    return results


def validate(results):
    reference = results[0]["metadata"]
    source_hashes = results[0]["experiment"]["sourceSha256"]
    identities = set()
    for result in results:
        metadata = result["metadata"]
        identity = (metadata["reader"], metadata["fork"])
        if identity in identities:
            raise SystemExit(f"Duplicate reader/fork: {identity}")
        identities.add(identity)
        if result["experiment"]["sourceSha256"] != source_hashes:
            raise SystemExit(f"{result['path']}: experiment sources differ")
        for key in ("java", "vm", "jvmArguments", "database", "jdbcDriver", "os", "arch",
                    "processors", "measurementSecondsPerRound", "warmupSecondsPerWorkloadAndConcurrency",
                    "warmupWindowsPerWorkloadAndConcurrency", "rounds", "secondLevelCache", "queryCache"):
            if metadata[key] != reference[key]:
                raise SystemExit(f"{result['path']}: mismatched condition {key}")
        for workload in WORKLOADS:
            if metadata[workload] != reference[workload]:
                raise SystemExit(f"{result['path']}: unequal fixture or response: {workload}")
        for measurement in result["measurements"]:
            if len(measurement["latencyNs"]) != measurement["requests"]:
                raise SystemExit(f"{result['path']}: missing latency samples")
            if not measurement["requests"] or measurement["wallNs"] <= 0:
                raise SystemExit(f"{result['path']}: empty or invalid measurement")
        expected = {(workload, concurrency, round_)
                    for workload in WORKLOADS for concurrency in (1, 4)
                    for round_ in range(1, metadata["rounds"] + 1)}
        actual = {(row["workload"], row["concurrency"], row["round"])
                  for row in result["measurements"]}
        if actual != expected or len(result["measurements"]) != len(expected):
            raise SystemExit(f"{result['path']}: incomplete or duplicate measurement windows")
        if len(result["diagnosticProbes"]) != 12:
            raise SystemExit(f"{result['path']}: incomplete diagnostic probes")
    for workload in WORKLOADS:
        probes = [p for r in results for p in r["diagnosticProbes"] if p["workload"] == workload]
        for key in ("converterCalls", "entityLoads", "sqlStatements"):
            if len({probe[key] for probe in probes}) != 1:
                raise SystemExit(f"Inconsistent persistence work for {workload}: {key}")
    print("PASS: identical fixtures and response hashes; equal converter calls, entity loads and SQL counts.")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("directory", nargs="?", type=Path, default=Path(__file__).parent / "results")
    args = parser.parse_args()
    results = load(args.directory)
    validate(results)
    readers = {reader: [r for r in results if r["metadata"]["reader"] == reader]
               for reader in ("legacy", "current")}
    if any(not values for values in readers.values()):
        raise SystemExit("Need both legacy and current results.")
    print("\nEach cell is the median across JVM forks; each fork pools its measured windows.")
    print("Percentiles use raw request latencies, not averages of window percentiles.")
    print("| Workload | Clients | Reader | Mean ms | P50 ms | P95 ms | P99 ms | req/s [fork range] |")
    print("|---|---:|---|---:|---:|---:|---:|---:|")
    for workload in WORKLOADS:
        for concurrency in (1, 4):
            rates = {}
            for reader in ("legacy", "current"):
                forks = []
                for result in readers[reader]:
                    rows = [r for r in result["measurements"]
                            if r["workload"] == workload and r["concurrency"] == concurrency]
                    samples = sorted(value for row in rows for value in row["latencyNs"])
                    forks.append({
                        "meanNs": statistics.mean(samples),
                        "p50Ns": samples[math.ceil(0.50 * len(samples)) - 1],
                        "p95Ns": samples[math.ceil(0.95 * len(samples)) - 1],
                        "p99Ns": samples[math.ceil(0.99 * len(samples)) - 1],
                        "requestsPerSecond": sum(r["requests"] for r in rows) * 1e9 / sum(r["wallNs"] for r in rows),
                    })
                metrics = {key: statistics.median(f[key] for f in forks) for key in forks[0]}
                rates[reader] = metrics["requestsPerSecond"]
                rate_values = [f["requestsPerSecond"] for f in forks]
                print(f"| {workload} | {concurrency} | {reader} "
                      + "".join(f"| {metrics[k] / 1e6:.3f} " for k in ("meanNs", "p50Ns", "p95Ns", "p99Ns"))
                      + f"| {rates[reader]:.1f} [{min(rate_values):.1f}-{max(rate_values):.1f}] |")
            print(f"\n{workload}, {concurrency} client(s): "
                  f"{(rates['current'] / rates['legacy'] - 1) * 100:+.1f}% median throughput.\n")
    print("\nDiagnostic probes (separate from timed workload; instrumentation affects time):")
    print("| Workload | Reader | Calls/request | SQL/request | Entities/request | Converter ms/request | Converter KiB/request |")
    print("|---|---|---:|---:|---:|---:|---:|")
    for workload in WORKLOADS:
        for reader in ("legacy", "current"):
            probes = [p for r in readers[reader] for p in r["diagnosticProbes"] if p["workload"] == workload]
            avg = {key: statistics.median(p[key] for p in probes)
                   for key in ("converterCalls", "sqlStatements", "entityLoads", "converterNs", "converterAllocatedBytes")}
            print(f"| {workload} | {reader} | {avg['converterCalls']:.0f} "
                  f"| {avg['sqlStatements']:.0f} | {avg['entityLoads']:.0f} "
                  f"| {avg['converterNs'] / 1e6:.3f} | {avg['converterAllocatedBytes'] / 1024:.1f} |")
    print("\nPer-fork/window results, including warmup drift:")
    for result in results:
        print(result["path"])
        for workload in WORKLOADS:
            for concurrency in (1, 4):
                windows = [r for r in result["measurements"]
                           if r["workload"] == workload and r["concurrency"] == concurrency]
                warmup = [r["requestsPerSecond"] for r in result["warmups"]
                          if r["workload"] == workload and r["concurrency"] == concurrency]
                rates = ", ".join(f"{r['requestsPerSecond']:.1f}" for r in windows)
                print(f"  {workload:12} clients={concurrency}: "
                      f"warmup {', '.join(f'{r:.1f}' for r in warmup)}; measured {rates} req/s")


if __name__ == "__main__":
    main()
