package no.ssb.klass.benchmark;

import no.ssb.klass.core.util.LegacyTranslatablePersistenceConverter;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(
        value = 3,
        jvmArgsAppend = {"-Xms256m", "-Xmx256m"})
public class TranslatableReadBenchmark {
    private static final int SIZE = 256;

    @Param({"short", "multilingual", "long", "escaped", "missing", "mixed"})
    public String corpus;

    private final TranslatablePersistenceConverter current = new TranslatablePersistenceConverter();
    private final TranslatablePersistenceConverter legacy =
            new LegacyTranslatablePersistenceConverter();
    private String[] inputs;

    @Setup
    public void setup() {
        Random random = new Random(20261102);
        List<String> values = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            String kind =
                    corpus.equals("mixed")
                            ? new String[] {"short", "multilingual", "long", "escaped", "missing"}
                                    [i % 5]
                            : corpus;
            String suffix = " " + random.nextInt(100_000);
            Translatable translation =
                    switch (kind) {
                        case "short" -> new Translatable("Jordbruk" + suffix, null, null);
                        case "multilingual" ->
                                new Translatable(
                                        "Jordbruk og tjenester" + suffix,
                                        "Jordbruk og tenester" + suffix,
                                        "Agriculture and services" + suffix);
                        case "long" ->
                                new Translatable(
                                        "Omfatter produksjon, tjenester og annen virksomhet.\n"
                                                        .repeat(80)
                                                + suffix,
                                        null,
                                        "Includes production, services and other activities.\n"
                                                        .repeat(80)
                                                + suffix);
                        case "escaped" ->
                                new Translatable(
                                        "A & B < C > D \"tekst\" 'tekst' \u00e6\u00f8\u00e5 "
                                                + suffix,
                                        "\u00c6\u00d8\u00c5 \u6f22\u5b57 \ud83d\ude00\n<no> &amp; "
                                                + suffix,
                                        "&#65; &unknown; </en>" + suffix);
                        case "missing" ->
                                switch (i % 4) {
                                    case 0 -> null;
                                    case 1 -> Translatable.empty();
                                    case 2 -> new Translatable("", "", "");
                                    default -> new Translatable(null, null, "Services" + suffix);
                                };
                        default -> throw new IllegalArgumentException(kind);
                    };
            String input = current.convertToDatabaseColumn(translation);
            if (!java.util.Objects.equals(
                    legacy.convertToEntityAttribute(input),
                    current.convertToEntityAttribute(input))) {
                throw new AssertionError("Reader mismatch at input " + i);
            }
            values.add(input);
        }
        inputs = values.toArray(String[]::new);
    }

    @Benchmark
    @OperationsPerInvocation(SIZE)
    public void current(Blackhole blackhole) {
        for (String input : inputs) {
            blackhole.consume(current.convertToEntityAttribute(input));
        }
    }

    @Benchmark
    @OperationsPerInvocation(SIZE)
    public void legacy(Blackhole blackhole) {
        for (String input : inputs) {
            blackhole.consume(legacy.convertToEntityAttribute(input));
        }
    }

    public static void main(String[] args) {
        System.out.printf(
                "Java %s; %s; %s %s %s%n",
                System.getProperty("java.runtime.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
        for (String corpus :
                new String[] {"short", "multilingual", "long", "escaped", "missing", "mixed"}) {
            TranslatableReadBenchmark benchmark = new TranslatableReadBenchmark();
            benchmark.corpus = corpus;
            benchmark.setup();
            int nulls = 0;
            int empty = 0;
            int entities = 0;
            java.util.IntSummaryStatistics lengths = new java.util.IntSummaryStatistics();
            for (String input : benchmark.inputs) {
                if (input == null) {
                    nulls++;
                } else {
                    lengths.accept(input.length());
                    empty += input.isEmpty() ? 1 : 0;
                    entities += input.indexOf('&') >= 0 ? 1 : 0;
                }
            }
            System.out.printf(
                    "%s: %d values, %d null, %d empty, %d containing '&'; "
                            + "non-null UTF-16 lengths min/mean/max %d/%.1f/%d%n",
                    corpus,
                    SIZE,
                    nulls,
                    empty,
                    entities,
                    lengths.getMin(),
                    lengths.getAverage(),
                    lengths.getMax());
        }
    }
}
