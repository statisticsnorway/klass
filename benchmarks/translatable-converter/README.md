# Translatable persistence read benchmark

This isolated JMH project compiles the **actual working-tree converter**, `Translatable`
and `Language`, alongside a frozen test-only copy of the original reader at
`bb11c5d877a9f3fce18789b39ff36fa97e793c86`. It is not a reactor module and adds no
application dependencies. JMH was not previously configured in this repository;
an isolated harness avoids ad-hoc timing loops, dead-code elimination, and adding
benchmark libraries to the production/test dependency graph.

## Results

The final reader was **1.64x to 19.37x faster** across these six synthetic
corpora, with lower allocation in every case. The largest gain required
**both** literal tag extraction and bypassing unnecessary entity decoding;
replacing regex alone did not remove the dominant long-text cost.

All timings below are means +/- JMH's **99.9% confidence half-width**, per
column read (15 measured iterations across three JVMs). The old/new confidence
intervals do not overlap in any corpus.

| Corpus | Legacy ns/read | Current ns/read | Speedup | Legacy B/read | Current B/read |
|---|---:|---:|---:|---:|---:|
| short | 272.10 +/- 4.88 | 30.27 +/- 0.46 | 8.99x | 887.91 | 80.00 |
| multilingual | 1795.83 +/- 28.74 | 111.49 +/- 0.93 | 16.11x | 1495.64 | 239.91 |
| long | 108615.77 +/- 4913.62 | 5608.37 +/- 53.12 | 19.37x | 34320.57 | 8440.04 |
| escaped | 2425.56 +/- 38.75 | 1480.91 +/- 24.92 | 1.64x | 4003.55 | 3355.54 |
| missing | 168.98 +/- 4.11 | 20.05 +/- 0.58 | 8.43x | 480.00 | 32.00 |
| mixed | 23251.41 +/- 264.30 | 1559.56 +/- 19.30 | 14.91x | 8207.47 | 2420.39 |

For example, the three multilingual fork means ranged from **1789.00 to
1804.71 ns/read** for legacy and **110.94 to 111.90 ns/read** for current.
Mixed fork means ranged from **23129.42 to 23343.09** and **1543.58 to
1568.64 ns/read**, respectively. The equivalent sequential conversion rate
for mixed inputs increased from **43,008 to 641,207 reads/s** (reciprocals of
mean latency, not an independent throughput experiment).

The final frozen legacy reader was close to the before-edit baseline:
272 vs 273 ns/read for short, 1796 vs 1762 for multilingual, 108616 vs
104542 for long, 2426 vs 2410 for escaped, 169 vs 169 for missing, and 23251
vs 22813 for mixed. Between-run drift is present, so the final table compares
both implementations under the same benchmark settings rather than taking
ratios against the earlier baseline.

Raw final samples and metadata are in [`results/comparison.json`](results/comparison.json);
[`compare.py`](compare.py) regenerates the table and every fork-mean range.
This is not evidence of an end-to-end API speedup; see the limitations below.
The follow-up [local HTTP experiment](api/README.md) measures the real API
request/response path separately rather than extrapolating these JMH results.

## Reproduce

From the repository root, select a Java 21 JDK (the measurements used Zulu
21.0.9+10-LTS):

```sh
export JAVA_HOME=/path/to/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
mvn -pl klass-shared -Dtest=TranslatablePersistenceConverterTest test
mvn -pl klass-api -am -DskipTests test
mvn -f benchmarks/translatable-converter/pom.xml package
java -cp benchmarks/translatable-converter/target/benchmarks.jar \
  no.ssb.klass.benchmark.TranslatableReadBenchmark
java -jar benchmarks/translatable-converter/target/benchmarks.jar \
  -prof gc -rf json \
  -rff benchmarks/translatable-converter/results/comparison.json
python3 benchmarks/translatable-converter/compare.py
```

Run the benchmark without concurrent builds, tests, or other CPU-heavy work.
`compare.py` reports latency, allocations, per-fork variability and reciprocal
conversion rates. It exits unsuccessfully unless all six corpora have lower
latency with non-overlapping JMH 99.9% confidence intervals and fewer allocated
bytes per read. This is a local comparison signal, **not** a portable CI timing
assertion or a service performance guarantee.

JMH settings: one thread; three fresh JVM forks per method/corpus; five
one-second warmup iterations and five one-second measurement iterations per
fork; fixed `-Xms256m -Xmx256m`; default Java 21 GC (G1); compiler blackholes.
Each invocation reads all 256 inputs, consumes every resulting `Translatable`
using JMH's `Blackhole`, and declares 256 operations. Times and allocation
normalization are therefore **per database-column conversion**, not per batch
or per individual language. Input construction and writer execution occur
outside timed sections. Setup verifies that both readers return equal results.
The benchmark excludes converter construction, database/JPA work and API work.

## Inputs

Synthetic, deterministic seed `20261102`; no production data or secrets.
These cases model short labels, multilingual names, long notes, escaped text,
and optional translations; their distribution is not an observed API workload.
Values vary by a generated numeric suffix.

| Corpus (256 values each) | Content | Non-null persisted UTF-16 length, min / mean / max |
|---|---|---:|
| short | Bokmal only, plain label | 21 / 22.9 / 23 |
| multilingual | Three plain labels | 104 / 109.6 / 110 |
| long | Two long notes, 80 repeated lines per language | 8346 / 8349.8 / 8350 |
| escaped | Three languages, XML-like text, entities, quotes, Norwegian/CJK/emoji | 169 / 174.6 / 175 |
| missing | 64 null, 64 empty fragments, 64 three-empty-language fragments, 64 English-only | 0 / 16.6 / 27 |
| mixed | Alternating the five cases above (52 short, 51 of each other kind); 13 null, 13 empty | 0 / 1819.6 / 8350 |

Only `escaped` inputs contain entity markers (256/256); the mixed set includes
51 such inputs. The mixed latency is dominated by the deliberately included
long notes and must not be used as an estimate for production.

## Experiment sequence

1. Ran the existing unit tests, then added compatibility tests and ran them
   against the unchanged converter before changing production code.
2. Built the harness and measured the unchanged actual converter:
   `results/baseline.csv`. Command below; two forks, three warmup iterations,
   five measurement iterations per fork. This established the symptom locally.
3. Changed only tag extraction to literal `indexOf` / `lastIndexOf`, retaining
   unconditional Commons XML unescaping. `results/extraction-only.csv` captures
   an exploratory single-fork run using the same inputs and three warmup
   iterations. This is directional evidence, not the final comparison.
4. Added the no-ampersand unescape fast path, re-ran correctness checks and
   compiled API/shared sources. Ran the final legacy/current comparison
   sequentially after compilation, using the stronger settings above.

```sh
# Step 2: this was run BEFORE production edits, with the original reader.
java -jar benchmarks/translatable-converter/target/benchmarks.jar '.*\.current' \
  -f 2 -wi 3 -prof gc -rf csv \
  -rff benchmarks/translatable-converter/results/baseline.csv

# Step 3: intermediate implementation, not the final working-tree converter.
java -jar benchmarks/translatable-converter/target/benchmarks.jar '.*\.current' \
  -f 1 -wi 3 -prof gc -rf csv \
  -rff benchmarks/translatable-converter/results/extraction-only.csv
```

The intermediate variant can be reconstructed by replacing the three
`unescape(...)` calls in `convertToEntityAttribute` with
`StringEscapeUtils.unescapeXml(...)` and removing the `input.isEmpty()` guard
in `TranslationPattern.find` (added when finalizing legacy fallback behavior);
do not change the preserved writer.
A preliminary final run was stopped because it overlapped compilation; it is
not part of the retained final comparison.

The ranked predictions were: replacing greedy regex searches would reduce
matching/scanning overhead; skipping entity decoding for text without `&`
would remove unnecessary copying/translation; escape-heavy values would
benefit less because decoding remains necessary. The first experiment improved
multilingual reads from 1762 to 874 ns/read and long reads from 104542 to
78808 ns/read, while removing approximately 648 B/read of matcher allocation
from those cases. The short-label exploratory run was noisy
(187 +/- 145 ns/read); final claims use the repeated final experiment instead.

## Compatibility

No schema migration or write-format change. The fragments remain ordered
`<no>...</no><nn>...</nn><en>...</en>` with absent languages omitted and
Commons `escapeXml11` on writes. Reads retain the original decoder for entity
content, including its exception behavior; no-entity text needs no decoding.
No XML parser is introduced: fragment leniency and historical matching
behavior remain intentional.

The unit suite covers null columns, empty fragments, absent versus empty
languages, exact serialized bytes, Unicode/supplementary characters, line
breaks, XML-like text, single-pass entity decoding, malformed/unknown entities,
out-of-range numeric entities, long text and incomplete fragments. It also
compares **20,000 deterministic generated inputs** with the frozen reader:
5,000 raw fragments and 5,000 writer-produced fragments for each of the
default `no` and importer `nb` configurations.

In particular, the greedy original captures the **first opening through the
last closing tag**, even across duplicate/nested tags; a first-closing-tag
scanner would silently change this. That behavior is preserved. The Stabas
importer's custom `nb` mapping and unchanged `no` write format are covered.
Even the public constructor's historical regex behavior for metacharacter
arguments is preserved via a fallback, keeping regex off normal `no`/`nb`,
`nn`, `en` read paths.

Validation on Java 21:

```sh
mvn -pl klass-shared fmt:format
mvn -f benchmarks/translatable-converter/pom.xml fmt:format
mvn -pl klass-shared fmt:check
mvn -f benchmarks/translatable-converter/pom.xml fmt:check
mvn -pl klass-shared -Dtest=TranslatablePersistenceConverterTest test
mvn -pl klass-api -am -DskipTests test
git diff --check
```

The converter suite passed all 16 tests, including the 20,000 generated
differential cases; shared and API production/test sources compiled. The
formatter completed successfully. No database or running API was required.

## Environment and interpretation

Measured on 2026-11-02, Apple M5, 16 GiB RAM, macOS 27.0.1 (26A434), arm64;
Zulu OpenJDK 21.0.9+10-LTS; Maven 3.10.0. The shell's default JDK was Temurin 25,
but all retained measurements and final compilation explicitly selected
Java 21. The original eight tests also passed on the default JDK before
selecting Java 21. The machine is a general-purpose developer machine, not a
dedicated performance host.

The final JSON preserves every per-iteration sample, fork grouping,
JMH confidence intervals, GC/allocation metrics and JVM arguments. Allocation
figures are JMH's normalized JVM allocation estimates, not retained heap
size. Reciprocals of mean ns/read are equivalent single-thread conversion
rates, not a separate saturated-throughput experiment. JVM, hardware, corpus,
GC and heap sizing affect the results.

These are **converter-level** gains only. There is no measurement of SQL,
Hibernate hydration, cache hit rates, concurrent requests, API latency, or
production workload frequencies, so no end-to-end `klass-api` speedup is
claimed. Escaped text still pays for Commons XML entity decoding.
