package no.ssb.klass.api.applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.ThreadMXBean;

import jakarta.persistence.EntityManagerFactory;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.LegacyTranslatablePersistenceConverter;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

import org.hibernate.SessionFactory;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.SpringBeanContainer;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Opt-in local HTTP experiment, not a timing-sensitive CI test. See
 * benchmarks/translatable-converter/api/README.md.
 */
@EnabledIfSystemProperty(named = "klass.converter.e2e", matches = "true")
@Import(TranslatableConverterHttpBenchmarkTest.ConverterConfiguration.class)
@SpringBootTest(
        classes = no.ssb.klass.api.applicationtest.config.ApplicationTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "server.address=127.0.0.1",
            "management.server.address=127.0.0.1",
            "management.server.port=0",
            "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
            "spring.jpa.properties.hibernate.cache.use_query_cache=false",
            "spring.jpa.properties.hibernate.generate_statistics=false",
            "spring.cache.type=none",
            "spring.jpa.show-sql=false",
            "logging.level.no.ssb.klass.api.controllers.ClassificationController=WARN"
        })
public class TranslatableConverterHttpBenchmarkTest extends AbstractRestApiApplicationTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String READER = System.getProperty("klass.converter.reader", "current");
    private static final int WARMUP_SECONDS = Integer.getInteger("klass.converter.warmup", 5);
    private static final int MEASURE_SECONDS = Integer.getInteger("klass.converter.seconds", 3);
    private static final int ROUNDS = Integer.getInteger("klass.converter.rounds", 3);
    private static final boolean SMOKE = Boolean.getBoolean("klass.converter.smoke");

    @Autowired private EntityManagerFactory entityManagerFactory;
    @Autowired private javax.sql.DataSource dataSource;

    private final List<Workload> workloads = new ArrayList<>();

    @Override
    @BeforeEach
    public void prepareTestData() {
        assertTrue(
                READER.equals("legacy") || READER.equals("current"), "Unknown reader: " + READER);
        applicationTestUtil.clearDatabase();
        template.executeWithoutResult(
                status -> {
                    User user = userRepository.save(TestUtil.createUser());
                    ClassificationFamily family =
                            classificationFamilyRepository.save(
                                    TestUtil.createClassificationFamily("Synthetic benchmark"));
                    workloads.add(createWorkload(family, user, "labels-100", 100, false));
                    if (!SMOKE) {
                        workloads.add(createWorkload(family, user, "labels-1000", 1_000, false));
                        workloads.add(createWorkload(family, user, "notes-1000", 1_000, true));
                    }
                });
    }

    private Workload createWorkload(
            ClassificationFamily family, User user, String name, int size, boolean notes) {
        ClassificationSeries series = TestUtil.createClassification(name);
        series.setContactPerson(user);
        family.addClassificationSeries(series);
        ClassificationVersion version =
                new ClassificationVersion(DateRange.create("2020-01-01", "2030-01-01"));
        series.addClassificationVersion(version);
        version.addLevel(new Level(1));
        MessageDigest fixture = sha256();
        TranslatablePersistenceConverter writer = new TranslatablePersistenceConverter();
        for (int i = 0; i < size; i++) {
            String code = String.format("%05d", i);
            Translatable title =
                    new Translatable(
                            "Tjenester og virksomhet \u00e6\u00f8\u00e5 " + code,
                            "Tenester og verksemd \u00e6\u00f8\u00e5 " + code,
                            "Services and activities " + code);
            Translatable shortName =
                    i % 2 == 0
                            ? new Translatable("Kort " + code, null, "Short " + code)
                            : Translatable.empty();
            Translatable text = Translatable.empty();
            if (notes) {
                String line =
                        i % 5 == 0
                                ? "Omfatter A & B < C > D, \"tekst\" og &#65;.\n"
                                : "Omfatter produksjon, tjenester og annen virksomhet.\n";
                String note = line.repeat(20) + code;
                text = new Translatable(note, note, note);
            }
            for (Translatable value : new Translatable[] {title, shortName, text}) {
                byte[] bytes =
                        writer.convertToDatabaseColumn(value)
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
                fixture.update(java.nio.ByteBuffer.allocate(4).putInt(bytes.length).array());
                fixture.update(bytes);
            }
            version.addClassificationItem(
                    new ConcreteClassificationItem(code, title, shortName, text), 1, null);
        }
        version.publish(Language.NB);
        version.publish(Language.EN);
        ClassificationSeries saved = seriesRepository.saveAndFlush(series);
        return new Workload(name, saved.getId(), size, HexFormat.of().formatHex(fixture.digest()));
    }

    @Test
    public void compareRealHttpReads() throws Exception {
        assertTrue(WARMUP_SECONDS > 0 && MEASURE_SECONDS > 0 && ROUNDS > 0);
        assertEquals(SMOKE ? 1 : 3, workloads.size());
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        assertFalse(sessionFactory.getSessionFactoryOptions().isSecondLevelCacheEnabled());
        Statistics statistics = sessionFactory.getStatistics();
        List<Map<String, Object>> measurements = new ArrayList<>();
        List<Map<String, Object>> warmups = new ArrayList<>();
        List<Map<String, Object>> probes = new ArrayList<>();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("reader", READER);
        metadata.put("java", System.getProperty("java.runtime.version"));
        metadata.put("vm", System.getProperty("java.vm.name"));
        metadata.put("jvmArguments", ManagementFactory.getRuntimeMXBean().getInputArguments());
        metadata.put("os", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        metadata.put("arch", System.getProperty("os.arch"));
        metadata.put("processors", Runtime.getRuntime().availableProcessors());
        metadata.put("warmupSecondsPerWorkloadAndConcurrency", WARMUP_SECONDS);
        metadata.put("measurementSecondsPerRound", MEASURE_SECONDS);
        metadata.put("rounds", ROUNDS);
        metadata.put("warmupWindowsPerWorkloadAndConcurrency", 2);
        metadata.put("fork", System.getProperty("klass.converter.fork", "smoke"));
        try (var connection = dataSource.getConnection()) {
            metadata.put("database", connection.getMetaData().getDatabaseProductVersion());
            metadata.put("jdbcDriver", connection.getMetaData().getDriverVersion());
        }
        metadata.put("secondLevelCache", false);
        metadata.put("queryCache", false);
        try (HttpClient client =
                HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build()) {
            for (Workload workload : workloads) {
                List<HttpRequest> requests = requests(workload);
                List<byte[]> bodies = new ArrayList<>();
                for (HttpRequest request : requests) {
                    HttpResponse<byte[]> response = send(client, request);
                    JsonNode codes = JSON.readTree(response.body()).get("codes");
                    assertEquals(workload.size, codes.size());
                    assertEquals("00000", codes.get(0).get("code").asText());
                    bodies.add(response.body());
                }
                Map<String, Object> hashes = new LinkedHashMap<>();
                for (int i = 0; i < requests.size(); i++) {
                    hashes.put(Integer.toString(i), digest(bodies.get(i)));
                }
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("name", workload.name);
                data.put("items", workload.size);
                data.put("fixtureSha256", workload.fixtureSha256);
                data.put("responseSha256", hashes);
                data.put("responseBytes", bodies.stream().map(b -> b.length).toList());
                metadata.put(workload.name, data);
                for (int concurrency : new int[] {1, 4}) {
                    for (int window = 1; window <= 2; window++) {
                        Map<String, Object> warmup =
                                runWindow(
                                        client,
                                        requests,
                                        bodies,
                                        concurrency,
                                        WARMUP_SECONDS,
                                        false);
                        warmup.put("workload", workload.name);
                        warmup.put("concurrency", concurrency);
                        warmup.put("window", window);
                        warmups.add(warmup);
                    }
                    for (int round = 1; round <= ROUNDS; round++) {
                        Map<String, Object> result =
                                runWindow(
                                        client,
                                        requests,
                                        bodies,
                                        concurrency,
                                        MEASURE_SECONDS,
                                        true);
                        result.put("workload", workload.name);
                        result.put("concurrency", concurrency);
                        result.put("round", round);
                        measurements.add(result);
                    }
                }
                statistics.setStatisticsEnabled(true);
                try {
                    ProbeConverter.probing = true;
                    try {
                        for (int i = 0; i < 8; i++) {
                            send(client, requests.get(i % requests.size()));
                        }
                    } finally {
                        ProbeConverter.probing = false;
                    }
                    for (int i = 0; i < requests.size(); i++) {
                        statistics.clear();
                        ProbeConverter.reset();
                        ProbeConverter.probing = true;
                        long start = System.nanoTime();
                        HttpResponse<byte[]> response;
                        try {
                            response = send(client, requests.get(i));
                        } finally {
                            ProbeConverter.probing = false;
                        }
                        long elapsed = System.nanoTime() - start;
                        assertArrayEquals(bodies.get(i), response.body());
                        long calls = ProbeConverter.calls.sum();
                        assertTrue(
                                calls >= 3L * workload.size,
                                "Persistence converter not reached: " + calls);
                        Map<String, Object> probe = new LinkedHashMap<>();
                        probe.put("workload", workload.name);
                        probe.put("request", i);
                        probe.put("httpNs", elapsed);
                        probe.put("converterCalls", calls);
                        probe.put("converterNs", ProbeConverter.nanos.sum());
                        probe.put("converterAllocatedBytes", ProbeConverter.bytes.sum());
                        probe.put("sqlStatements", statistics.getPrepareStatementCount());
                        probe.put("entityLoads", statistics.getEntityLoadCount());
                        probes.add(probe);
                    }
                } finally {
                    statistics.setStatisticsEnabled(false);
                }
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("metadata", metadata);
        result.put("warmups", warmups);
        result.put("measurements", measurements);
        result.put("diagnosticProbes", probes);
        Path output =
                Path.of(System.getProperty("klass.converter.output", "target/converter-http.json"));
        Files.createDirectories(output.toAbsolutePath().getParent());
        JSON.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), result);
        System.out.println("CONVERTER_HTTP_RESULT " + output.toAbsolutePath());
    }

    private List<HttpRequest> requests(Workload workload) {
        String base = "http://127.0.0.1:" + port + REQUEST + "/" + workload.id;
        return List.of(
                request(base + "/codesAt?date=2025-01-01&language=nb"),
                request(base + "/codesAt?date=2025-01-01&language=en"),
                request(base + "/codes?from=2024-01-01&to=2025-01-01&language=nb"),
                request(base + "/codes?from=2024-01-01&to=2025-01-01&language=en"));
    }

    private HttpRequest request(String uri) {
        return HttpRequest.newBuilder(URI.create(uri))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .build();
    }

    private static HttpResponse<byte[]> send(HttpClient client, HttpRequest request)
            throws Exception {
        HttpResponse<byte[]> response =
                client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("json"));
        return response;
    }

    private Map<String, Object> runWindow(
            HttpClient client,
            List<HttpRequest> requests,
            List<byte[]> expected,
            int concurrency,
            int seconds,
            boolean retain)
            throws Exception {
        CountDownLatch ready = new CountDownLatch(concurrency);
        CountDownLatch start = new CountDownLatch(1);
        try (var workers = Executors.newFixedThreadPool(concurrency)) {
            List<Future<List<Long>>> futures = new ArrayList<>();
            for (int worker = 0; worker < concurrency; worker++) {
                int offset = worker;
                futures.add(
                        workers.submit(
                                () -> {
                                    ready.countDown();
                                    start.await();
                                    long deadline =
                                            System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
                                    List<Long> samples = new ArrayList<>();
                                    int sequence = offset;
                                    do {
                                        int index = sequence++ % requests.size();
                                        long begin = System.nanoTime();
                                        HttpResponse<byte[]> response =
                                                send(client, requests.get(index));
                                        long elapsed = System.nanoTime() - begin;
                                        assertArrayEquals(expected.get(index), response.body());
                                        samples.add(elapsed);
                                    } while (System.nanoTime() < deadline);
                                    return samples;
                                }));
            }
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            long begin = System.nanoTime();
            start.countDown();
            List<Long> samples = new ArrayList<>();
            for (Future<List<Long>> future : futures) {
                samples.addAll(future.get(seconds + 60L, TimeUnit.SECONDS));
            }
            long elapsed = System.nanoTime() - begin;
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("requests", samples.size());
            result.put("wallNs", elapsed);
            result.put("requestsPerSecond", samples.size() * 1e9 / elapsed);
            long[] sorted = samples.stream().mapToLong(Long::longValue).sorted().toArray();
            if (retain) {
                result.put("latencyNs", sorted);
            }
            result.put("meanNs", Arrays.stream(sorted).average().orElseThrow());
            result.put("p50Ns", percentile(sorted, 0.50));
            result.put("p95Ns", percentile(sorted, 0.95));
            result.put("p99Ns", percentile(sorted, 0.99));
            return result;
        }
    }

    private static long percentile(long[] sorted, double fraction) {
        return sorted[(int) Math.ceil(fraction * sorted.length) - 1];
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String digest(byte[] bytes) {
        return HexFormat.of().formatHex(sha256().digest(bytes));
    }

    private record Workload(String name, long id, int size, String fixtureSha256) {}

    @TestConfiguration
    static class ConverterConfiguration {
        @Bean
        public HibernatePropertiesCustomizer benchmarkConverter(
                ConfigurableListableBeanFactory beanFactory) {
            return properties ->
                    properties.put(
                            "hibernate.resource.beans.container",
                            new ConverterBeanContainer(new SpringBeanContainer(beanFactory)));
        }

        @Bean
        public org.springframework.boot.web.server.WebServerFactoryCustomizer<
                        org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory>
                benchmarkAccessLogging() {
            return factory ->
                    factory.addContextCustomizers(
                            context -> {
                                for (org.apache.catalina.Valve valve :
                                        context.getPipeline().getValves()) {
                                    if (valve
                                            instanceof ch.qos.logback.access.tomcat.LogbackValve) {
                                        context.getPipeline().removeValve(valve);
                                    }
                                }
                            });
        }
    }

    private static final class ConverterBeanContainer implements BeanContainer {
        private final BeanContainer delegate;
        private final ProbeConverter converter = new ProbeConverter();

        private ConverterBeanContainer(BeanContainer delegate) {
            this.delegate = delegate;
        }

        @Override
        public <B> ContainedBean<B> getBean(
                Class<B> type, LifecycleOptions options, BeanInstanceProducer fallback) {
            return type == TranslatablePersistenceConverter.class
                    ? () -> type.cast(converter)
                    : delegate.getBean(type, options, fallback);
        }

        @Override
        public <B> ContainedBean<B> getBean(
                String name,
                Class<B> type,
                LifecycleOptions options,
                BeanInstanceProducer fallback) {
            return type == TranslatablePersistenceConverter.class
                    ? () -> type.cast(converter)
                    : delegate.getBean(name, type, options, fallback);
        }

        @Override
        public void stop() {
            delegate.stop();
        }
    }

    static class ProbeConverter extends TranslatablePersistenceConverter {
        private final TranslatablePersistenceConverter reader =
                READER.equals("legacy")
                        ? new LegacyTranslatablePersistenceConverter()
                        : new TranslatablePersistenceConverter();
        private static final ThreadMXBean ALLOCATIONS =
                ManagementFactory.getPlatformMXBean(ThreadMXBean.class);
        static volatile boolean probing;
        static final LongAdder calls = new LongAdder();
        static final LongAdder nanos = new LongAdder();
        static final LongAdder bytes = new LongAdder();

        static void reset() {
            calls.reset();
            nanos.reset();
            bytes.reset();
        }

        @Override
        public Translatable convertToEntityAttribute(String dbData) {
            if (!probing) {
                return reader.convertToEntityAttribute(dbData);
            }
            assertTrue(ALLOCATIONS.isThreadAllocatedMemorySupported());
            assertTrue(ALLOCATIONS.isThreadAllocatedMemoryEnabled());
            long thread = Thread.currentThread().threadId();
            long allocated = ALLOCATIONS.getThreadAllocatedBytes(thread);
            long start = System.nanoTime();
            Translatable result = reader.convertToEntityAttribute(dbData);
            nanos.add(System.nanoTime() - start);
            bytes.add(ALLOCATIONS.getThreadAllocatedBytes(thread) - allocated);
            calls.increment();
            return result;
        }
    }
}
