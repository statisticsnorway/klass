# Local end-to-end HTTP comparison

This experiment measures the effect of the converter change through **real
loopback HTTP requests**, Tomcat, Spring MVC, the real classification service,
Hibernate, disposable PostgreSQL, DTO conversion, JSON serialization and
receipt of the complete response body. It is not a controller mock, direct
service timing, JMH extrapolation, or production performance claim.

## Results

The local HTTP experiment found a clear benefit for the larger fixtures:
**48% more serialized throughput for 1,000 labels** and **91% more for 1,000
note-heavy items**, using medians across three JVMs per reader. Gains were
much smaller and less consistent for the 100-item fixture. These results are
measured HTTP effects, separate from the earlier converter-only JMH results.

All six runs completed, with **90,447 measured HTTP requests** across 108
measurement windows. All fixture and response hashes matched, and both
readers performed equal SQL/entity/converter work. The complete generated
report, window-level variability and diagnostic attribution are in
[`results/summary.txt`](results/summary.txt); compressed raw samples and
metadata are alongside it.

The following values are medians across three JVM forks. Each fork pools
its three measurement windows. P95/P99 come from raw per-request latencies,
not averages of window percentiles.

| Workload | Clients | Mean ms, old -> new | P95 ms, old -> new | P99 ms, old -> new | req/s, old -> new | Throughput change |
|---|---:|---:|---:|---:|---:|---:|
| 100 labels | 1 | 5.165 -> 4.605 | 5.792 -> 5.465 | 7.115 -> 7.732 | 193.4 -> 216.9 | +12.2% |
| 100 labels | 4 | 8.742 -> 7.421 | 10.541 -> 9.925 | 12.106 -> 11.557 | 456.9 -> 538.0 | +17.8% |
| 1,000 labels | 1 | 16.246 -> 10.967 | 17.279 -> 12.029 | 19.076 -> 13.773 | 61.4 -> 90.9 | +48.0% |
| 1,000 labels | 4 | 27.599 -> 19.029 | 30.551 -> 21.533 | 32.483 -> 25.600 | 144.5 -> 209.2 | +44.8% |
| 1,000 note-heavy items | 1 | 225.135 -> 117.560 | 232.388 -> 123.200 | 251.907 -> 128.276 | 4.4 -> 8.5 | +91.3% |
| 1,000 note-heavy items | 4 | 395.531 -> 206.951 | 419.112 -> 229.493 | 430.482 -> 248.770 | 10.1 -> 19.3 | +90.9% |

Important variability:

* For 1,000 labels with one client, the per-JVM throughput ranges were
  **60.6-64.5 old vs 89.4-91.8 new req/s**. The three paired improvements
  were **+42.2%, +48.0%, +47.4%**. Mean HTTP latency fell about 32.5%.
* For 1,000 labels with four clients, ranges were **128.7-189.1 vs
  206.2-224.5 req/s**; paired gains were **+18.7%, +42.7%, +62.6%**.
  The median improvement is real in this setup, but its exact size is
  sensitive to the run.
* For note-heavy items with one client, ranges were **4.1-4.5 vs 7.9-8.5
  req/s**; paired gains were **+91.5%, +77.0%, +109.0%**.
  Four-client ranges were **9.9-12.9 vs 18.4-19.5 req/s**, with paired
  gains **+51.0%, +90.9%, +86.2%**. Mean latency fell about 48%.
* For 100 labels with four clients, ranges **overlap**: **444.1-603.2 old
  vs 505.7-542.0 new req/s**. One pair regressed **10.2%**; the others
  improved **17.8% and 13.9%**. Do not treat the +17.8% median as a
  repeatable guaranteed improvement.
* The 100-label single-client **P99 increased**, despite better median/mean
  throughput. Small-request tail latency did not consistently improve.
  Its measured windows also show continued warmup/drift; the larger
  serialized label workload was substantially more stable.

The notes workload has only **62-69 old / 120-129 new serialized requests
per fork**, and **156-200 / 280-300 four-client requests per fork**.
Its P99 estimates therefore depend on very few tail requests. Even with
90,447 requests overall, there are only three independent JVM runs per
variant; no production significance or tail-SLO claim follows from this.

Separate warmed diagnostic probes support attribution:

| Fixture | Converter time ms/request, old -> new | Converter allocated KiB/request, old -> new |
|---|---:|---:|
| 100 labels | 0.817 -> 0.059 | 457.0 -> 69.6 |
| 1,000 labels | 6.134 -> 0.473 | 4,514.1 -> 688.4 |
| 1,000 note-heavy items | 128.784 -> 20.540 | 45,482.8 -> 25,129.0 |

The larger measured HTTP reductions are consistent with avoiding converter
work and allocations, rather than issuing fewer queries. These probes are
instrumented and separate from the main measurements, so their times are
not exact decompositions of the HTTP latency.

## Reproduce

From the repository root, with Java 21 and Python 3 available:

```sh
export JAVA_HOME=/path/to/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"

# Optional short preflight of all data sets and both readers; NOT final evidence.
python3 benchmarks/translatable-converter/api/run.py --quick \
  --output klass-api/target/converter-http-preflight
python3 benchmarks/translatable-converter/api/summarize.py \
  klass-api/target/converter-http-preflight

# Full experiment: about 16 minutes on the measured machine.
# Existing results are never overwritten: choose another directory for a rerun.
python3 benchmarks/translatable-converter/api/run.py \
  --output klass-api/target/converter-http-rerun
python3 benchmarks/translatable-converter/api/summarize.py \
  klass-api/target/converter-http-rerun

# Recompute the checked-in evidence without starting any services.
python3 benchmarks/translatable-converter/api/summarize.py
```

No manually provisioned database, Docker container, deployment credentials,
production data or public network listener is required. Maven may download
normal dependencies and the embedded PostgreSQL executable on the first run.
Both application and management servers bind to `127.0.0.1` with random
ports; Zonky PostgreSQL listens on loopback. Processes shut down when each
test JVM exits. Do not run builds, tests or other CPU-heavy work concurrently.

The runner starts Maven separately for each run, with:

```sh
JAVA_TOOL_OPTIONS="-Xms512m -Xmx512m -XX:+UseG1GC" \
mvn -q -pl klass-api -am \
  -Dtest=TranslatableConverterHttpBenchmarkTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dklass.converter.e2e=true \
  -Dklass.converter.reader=legacy \
  -Dklass.converter.fork=1 \
  -Dklass.converter.output=target/converter-http/legacy-1.json \
  -Dklass.converter.warmup=5 \
  -Dklass.converter.seconds=5 \
  -Dklass.converter.rounds=3 test
```

Use `current` for the optimized reader. The test is **skipped by default**;
there is no timing assertion in normal CI and no production configuration
change. The runner explicitly selects the Java 21 installation in `JAVA_HOME`,
sets identical JVM settings for all runs, checks completeness and compresses
the raw JSON. It records source SHA-256 hashes, production revision, command,
run order and actual JVM/OS/database metadata alongside the measurements.
Logs and uncompressed intermediate files remain under the ignored
`klass-api/target/converter-http/` directory.

## Controlled comparison

The production converter commit is `e48ee876ed2ed0c03de021b7aabb5f6fee88734e`.
Every run uses the same application source and test configuration. A
test-only Hibernate `BeanContainer` selects either:

* `LegacyTranslatablePersistenceConverter`: the frozen original read
  implementation from `bb11c5d877a9f3fce18789b39ff36fa97e793c86`, also used
  by the differential tests and JMH benchmark; or
* the actual optimized `TranslatablePersistenceConverter`.

The legacy class inherits the unchanged writer. No controller, query, domain
service or serialization code is replaced. Both readers go through an
identical wrapper with a disabled diagnostic branch in timed requests.
This keeps the reader as the experimental variable, rather than comparing
different application deployments with potentially different configuration.
The frozen read and `find` methods were also compared text-for-text against
the original Git revision and matched exactly.

The Spring setup is the repository's existing `ApplicationTestConfig` and
`AbstractRestApiApplicationTest`: real repositories/services, schema created
by Hibernate, Zonky embedded PostgreSQL, mocked search and mail dependencies.
Search/mail are not involved in the measured read routes. The fixture is
persisted through the domain hierarchy and JPA repositories and committed
before HTTP requests; measured calls are not served from the test transaction.

Three fresh test JVMs **per reader**, run sequentially in this order:
`legacy-1`, `current-1`, `current-2`, `legacy-2`, `legacy-3`, `current-3`.
Each uses a new disposable database populated with equivalent deterministic
data. Reversing the middle pair reduces order bias but does not eliminate
system noise or thermal drift.

For each workload, run one client and then four clients. Each combination
gets **two five-second warmup windows** followed by **three five-second
measurement windows**. JVM startup, database creation, fixture insertion,
initial response validation, warmup and diagnostics are excluded. Warmup
statistics are retained so drift can be inspected rather than hidden.

The client uses Java's HTTP/1.1 client, persistent connections, no HTTP
compression, no retries and no think time. Each closed-loop worker issues
one request at a time; four workers produce up to four concurrent requests.
They cycle through the following mix (approximately 25% each, bounded by the
last incomplete cycle per worker):

```text
/api/klass/v1/classifications/{id}/codesAt?date=2025-01-01&language=nb
/api/klass/v1/classifications/{id}/codesAt?date=2025-01-01&language=en
/api/klass/v1/classifications/{id}/codes?from=2024-01-01&to=2025-01-01&language=nb
/api/klass/v1/classifications/{id}/codes?from=2024-01-01&to=2025-01-01&language=en
```

Latency covers synchronous send through complete body receipt, including
small HTTP status/content-type assertions. Byte-for-byte response validation
and sample-list bookkeeping happen after the latency timer; they are part of
the closed-loop throughput budget. Throughput is completed requests divided
by actual window wall time, including the final in-flight completions.
It is **not** an open-loop capacity/SLO or saturation test.

## Workloads and cache conditions

`ClassificationController.codesAt` and `.codes` both call
`ClassificationServiceImpl.findClassificationCodes`, which walks published
versions and items. `ConcreteClassificationItem` has three translated
columns: official name, short name and notes. Returning one language still
hydrates and decodes the stored multilingual values. The test uses one
published version and one level per series, rather than inventing unrelated
query behavior.

| Workload | Items | Data | JSON response size across four requests |
|---|---:|---|---:|
| labels-100 | 100 | Three-language names; half have NB/EN short names; empty notes | 17,161-25,811 bytes |
| labels-1000 | 1,000 | Same label distribution | 171,511-258,011 bytes |
| notes-1000 | 1,000 | Same names plus three-language notes, 20 repeated lines per language; 20% contain XML-like content/entities | 1,184,511-1,271,011 bytes |

Data is synthetic and fixed by item index, not sampled from production.
Names include Norwegian Unicode. Empty translations are retained, and
notes exercise both the no-entity fast path and the unchanged entity decoder.
The three note-language strings deliberately match, simplifying deterministic
content; their frequency and length are not a measured real-world workload.
Fixture hashes cover length-prefixed UTF-8 persisted translation fragments.

**Caches:** Hibernate second-level/query caches and Spring caches are disabled;
the API already disables Hibernate L2 in its normal configuration. Each HTTP
request uses its own persistence context; there is no cross-request L1 reuse.
PostgreSQL buffers, OS filesystem cache, connection state and JVM code are
warm. This is a **warm-database, repeatedly hydrated read** experiment, not a
cold-start or database-cache eviction experiment. No cache-hit-only speedup
is claimed.

Controller timing logs and Tomcat access logging are disabled identically
for both readers to avoid per-request console I/O dominating the result.
All application filters and JSON serialization remain in place. Production
logging costs would contribute additional unchanged overhead.

## Correctness and attribution

Every request must return HTTP 200 JSON and byte-for-byte match the initial
response for that exact route/language. The initial result must contain the
expected item count and first code. SHA-256 response hashes are retained.
The summarizer refuses mismatched fixture hashes, response hashes, environment
conditions, incomplete samples or different persistence work between readers.

After measurement, eight diagnostic requests warm the instrumented path,
then four probes (one per route/language) enable Hibernate statistics and
converter counters, elapsed time and thread-allocated-byte measurement.
Statistics and diagnostic instrumentation are disabled during timed windows.
The probes assert at least three converter calls per item. Thus an incorrect
converter registration or accidentally cached/non-persistent path cannot
produce success-shaped benchmark results.

The measured path actually invokes the converter **616 times for 100 items**
and **6,016 times for 1,000 items**; both readers issue **5 SQL statements** and
load **105 / 1,005 entities** per probe. Counts exceed the three item columns
because Hibernate also converts snapshot/deep-copy values and metadata.
These are observed counts, not an assumption that one entity equals one
converter call.

Diagnostic allocations cover only the executing converter calls, not the
whole HTTP request, client, JDBC, entities, DTOs or JSON. Diagnostic times
include timer/counter effects and must not be subtracted from the main HTTP
timings as an exact accounting identity. They support attribution, not an
independent precise CPU profile.

## Environment and limitations

Measured 2026-11-02 on Apple M5, 16 GiB RAM, 10 available logical processors,
macOS 27.0.1 (26A434), arm64; Zulu Java 21.0.9+10-LTS, Maven 3.10.0,
Spring Boot 3.5.16, Hibernate 6.6.53.Final, Tomcat 10.1.59, PostgreSQL JDBC
42.7.14. Heap fixed to 512 MiB with G1 for every test JVM.

The repository's embedded provider starts **PostgreSQL 14.22, an x86_64 build**
on this arm64 host, not production's PostgreSQL 17. Tests use Hibernate
`create-drop`, not production Flyway migrations. Local PostgreSQL latency,
the embedded datasource, native/emulated database execution and same-host
resource contention affect the amount of time left for the converter to
dominate. These conditions are identical across variants, but they limit
generalization.

Client and server share a JVM and machine. This preserves a complete HTTP
path but is not a remote load generator or a replica of the deployed WAR,
reverse proxy, TLS, Cloud SQL network, CPU limits, cache distribution,
traffic mix or production concurrency. Repeated fixtures keep database
buffers hot; a network/database-bound deployment could see less benefit.

The developer machine is not a dedicated performance host. Use across-fork
ranges and window-level drift, not just pooled request counts, to judge
variability. Requests within a process are correlated; thousands of requests
are not thousands of independent experimental runs. P99 estimates,
particularly for the lower-throughput notes workload, have limited sample
counts and are descriptive rather than production-tail guarantees.

The main summary pools raw samples within each JVM, then reports the median
of each metric across the three JVMs; throughput ranges are ranges of
per-JVM aggregate rates. It does not attach unsupported statistical
significance or confidence claims to three local runs.

## Setup checks and failed attempts

The existing `RestApiCodeIntegrationTest#restServiceCodesJSON` passed first,
confirming that disposable PostgreSQL and a real embedded HTTP server work
without deployment credentials.

During harness construction, smoke checks caught three setup problems:
the local `@SpringBootTest` override initially selected mock web mode; the
overridden fixture method needed its own `@BeforeEach`; and merely replacing
a Spring converter bean did not select Hibernate's converter instance.
The final harness explicitly selects `RANDOM_PORT`, asserts the fixture count,
and supplies a test-only Hibernate `BeanContainer`. The converter-call
assertion failed before that registration was corrected and passed afterward.
No measurements from those incomplete setups are retained as evidence.

A successful short preflight then exercised both readers, all three fixtures,
both concurrency levels and every request type, and verified identical
responses and persistence work. Its one-second windows are not used in final
performance claims.

After measurement, the Java 21 regression command below passed the 16
converter tests (including 20,000 differential inputs) and all 10 existing
codes-endpoint integration tests. The opt-in HTTP benchmark was skipped
without its enabling property. API/shared production and test compilation
also completed as part of these runs.

```sh
mvn -pl klass-api -am \
  -Dtest=TranslatablePersistenceConverterTest,RestApiCodeIntegrationTest,TranslatableConverterHttpBenchmarkTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dserver.address=127.0.0.1 -Dmanagement.server.address=127.0.0.1 test
```
