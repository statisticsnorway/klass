#!/usr/bin/env python3
"""Run isolated, alternating legacy/current local HTTP experiments."""

import argparse
import gzip
import hashlib
import json
import os
from pathlib import Path
import subprocess


ROOT = Path(__file__).resolve().parents[3]
HERE = Path(__file__).resolve().parent


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--quick", action="store_true", help="Preflight all fixtures; not final evidence")
    parser.add_argument("--output", type=Path, default=HERE / "results")
    args = parser.parse_args()
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=True)
    if "JAVA_HOME" not in os.environ:
        raise SystemExit("Select a Java 21 JDK with JAVA_HOME before running.")
    env = dict(os.environ)
    env["PATH"] = str(Path(env["JAVA_HOME"]) / "bin") + os.pathsep + env["PATH"]
    env["JAVA_TOOL_OPTIONS"] = "-Xms512m -Xmx512m -XX:+UseG1GC"
    java = subprocess.check_output(["java", "-version"], env=env, stderr=subprocess.STDOUT, text=True)
    if 'version "21.' not in java:
        raise SystemExit("This experiment requires Java 21:\n" + java)
    print(java, flush=True)
    revision = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()
    runs = [("legacy", 1), ("current", 1)]
    if not args.quick:
        runs += [("current", 2), ("legacy", 2), ("legacy", 3), ("current", 3)]
    work = ROOT / "klass-api" / "target" / "converter-http"
    work.mkdir(parents=True, exist_ok=True)
    for reader, fork in runs:
        name = f"{reader}-{fork}"
        destination = output / f"{name}.json.gz"
        if destination.exists():
            raise SystemExit(f"Refusing to overwrite retained evidence: {destination}")
        temporary = work / f"{name}.json"
        log = work / f"{name}.log"
        command = [
            "mvn", "-q", "-pl", "klass-api", "-am",
            "-Dtest=TranslatableConverterHttpBenchmarkTest",
            "-Dsurefire.failIfNoSpecifiedTests=false",
            "-Dklass.converter.e2e=true",
            f"-Dklass.converter.reader={reader}",
            f"-Dklass.converter.fork={fork}",
            f"-Dklass.converter.output={temporary.relative_to(ROOT / 'klass-api')}",
            f"-Dklass.converter.warmup={1 if args.quick else 5}",
            f"-Dklass.converter.seconds={1 if args.quick else 5}",
            f"-Dklass.converter.rounds={1 if args.quick else 3}",
            "test",
        ]
        print(f"Running {name}; local log: {log}", flush=True)
        with log.open("w") as stream:
            completed = subprocess.run(command, cwd=ROOT, env=env, stdout=stream, stderr=subprocess.STDOUT)
        if completed.returncode:
            print("\n".join(log.read_text().splitlines()[-65:]))
            raise SystemExit(completed.returncode)
        data = json.loads(temporary.read_text())
        expected = 6 if args.quick else 18
        if len(data["measurements"]) != expected or len(data["diagnosticProbes"]) != 12:
            raise SystemExit("Incomplete experiment; refusing to retain it as evidence.")
        data["experiment"] = {
            "revision": revision,
            "quick": args.quick,
            "command": command,
            "javaToolOptions": env["JAVA_TOOL_OPTIONS"],
            "runOrder": runs,
            "sourceSha256": {
                str(path.relative_to(ROOT)): hashlib.sha256(path.read_bytes()).hexdigest()
                for path in (
                    ROOT / "klass-api/src/test/java/no/ssb/klass/api/applicationtest/TranslatableConverterHttpBenchmarkTest.java",
                    ROOT / "klass-shared/src/main/java/no/ssb/klass/core/util/TranslatablePersistenceConverter.java",
                    ROOT / "klass-shared/src/test/java/no/ssb/klass/core/util/LegacyTranslatablePersistenceConverter.java",
                    Path(__file__).resolve(),
                )
            },
        }
        with gzip.GzipFile(destination, "wb", mtime=0) as stream:
            stream.write((json.dumps(data, indent=2) + "\n").encode())
        print(f"Saved {destination}", flush=True)


if __name__ == "__main__":
    main()
