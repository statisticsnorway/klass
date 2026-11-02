#!/usr/bin/env python3
"""Summarize JMH measurements; fail if a corpus has no measured improvement."""

import json
import statistics
import sys
from pathlib import Path


def main():
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(__file__).parent / "results/comparison.json"
    results = json.loads(path.read_text())
    if any(row["mode"] != "avgt" or row["primaryMetric"]["scoreUnit"] != "ns/op" for row in results):
        raise SystemExit("Expected JMH average-time results in ns/op.")
    by_case = {
        (row["benchmark"].rsplit(".", 1)[1], row["params"]["corpus"]): row
        for row in results
    }
    print("| Corpus | Legacy ns/read (99.9% CI) | Current ns/read (99.9% CI) | Speedup | Legacy B/read | Current B/read |")
    print("|---|---:|---:|---:|---:|---:|")
    regressions = []
    for corpus in ("short", "multilingual", "long", "escaped", "missing", "mixed"):
        old = by_case["legacy", corpus]
        new = by_case["current", corpus]
        latency_old = old["primaryMetric"]
        latency_new = new["primaryMetric"]
        bytes_old = old["secondaryMetrics"]["gc.alloc.rate.norm"]["score"]
        bytes_new = new["secondaryMetrics"]["gc.alloc.rate.norm"]["score"]
        print(
            f"| {corpus} | {latency_old['score']:.2f} ± {latency_old['scoreError']:.2f} "
            f"| {latency_new['score']:.2f} ± {latency_new['scoreError']:.2f} "
            f"| {latency_old['score'] / latency_new['score']:.2f}x "
            f"| {bytes_old:.2f} | {bytes_new:.2f} |"
        )
        if latency_new["scoreConfidence"][1] >= latency_old["scoreConfidence"][0]:
            regressions.append(f"{corpus}: latency confidence intervals overlap or regress")
        if bytes_new >= bytes_old:
            regressions.append(f"{corpus}: allocation did not improve")
    print("\nFork-mean ranges and equivalent single-thread conversion rates:")
    for corpus in ("short", "multilingual", "long", "escaped", "missing", "mixed"):
        for reader in ("legacy", "current"):
            metric = by_case[reader, corpus]["primaryMetric"]
            means = [statistics.mean(fork) for fork in metric["rawData"]]
            print(
                f"{corpus:12} {reader:7}: {min(means):10.2f}..{max(means):10.2f} ns/read; "
                f"{1e9 / metric['score']:12,.0f} reads/s (reciprocal of mean latency)"
            )
    if regressions:
        raise SystemExit("\n".join(regressions))
    print("\nPASS: lower latency with non-overlapping JMH intervals and fewer bytes/read in every corpus.")


if __name__ == "__main__":
    main()
