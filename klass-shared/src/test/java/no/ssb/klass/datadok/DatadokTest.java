package no.ssb.klass.datadok;

import static com.google.common.base.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import no.ssb.klass.core.model.Language;

// This test is used for filtering codelists from datadok. Probably delete after migration from Datadok,
// if deleted remember to also delete resources datadoc_codes and datadok_metadata
@Disabled
public class DatadokTest {
    private static final Logger log = LoggerFactory.getLogger(DatadokTest.class);

    @Test
    public void test() throws Exception {
        StabasReader reader = new StabasReader();
        List<Codelist> stabasCodelists = reader.readStabas(Language.NB);
        stabasCodelists.addAll(reader.readStabas(Language.EN));
        stabasCodelists.addAll(reader.readStabas(Language.NN));
        CodelistCollection codelists = createCodelists();
        codelists = codelists
                .printCount("Total")
                .removeSingleCodes()
                .removeDuplicates()
                // .removeCodelistsWithNonNumericCodes()
                .removeCodelistsWithNumericCodeTitles()
                .removeCodelistWithCodeTitle("d")
                .removeCodelistWithCodeTitle("2.")
                .removeExtracts()
                .removeAlmostExtracts()
                .removeStabasExtracts(stabasCodelists)
                .removeAlmostStabasExtracts(stabasCodelists)
                .printCount("Reduced")
                .printMostUsedCodes()
                .printDistribution()
                // .printCodelistsWithNumberOfCodes(3)
                .printCodelistsByOwner();
    }

    private CodelistCollection createCodelists() throws Exception {
        File codefile = openFile("datadok/datadok_codes.csv");
        File metadataFile = openFile("datadok/datadok_metadata.csv");
        CodelistCollection codelists = CodelistCollection.toCodelists(readCodefile(codefile), readMetadatafile(
                metadataFile));
        return codelists;
    }

    private File openFile(String file) {
        try {
            Resource resource = new ClassPathResource(file);
            return resource.getFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file", e);
        }
    }

    private List<Code> readCodefile(File codefile) throws Exception {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(Code.class).withHeader();
        ObjectReader reader = mapper.readerFor(Code.class).with(schema)
                .withoutRootName();
        MappingIterator<Code> iterator = reader.readValues(codefile);
        return iterator.readAll();
    }

    private List<Metadata> readMetadatafile(File codefile) throws Exception {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(Metadata.class).withHeader();
        ObjectReader reader = mapper.readerFor(Metadata.class).with(schema)
                .withoutRootName();
        MappingIterator<Metadata> iterator = reader.readValues(codefile);
        return iterator.readAll();
    }

    private static class CodelistCollection {
        private final Collection<Codelist> codelists;

        CodelistCollection(Collection<Codelist> codelists) {
            this.codelists = codelists;
        }

        public CodelistCollection printCodelistsByOwner() {
            StringBuilder builder = new StringBuilder();
            for (Entry<String, List<Codelist>> codelistsByOwner : indexByOwner().entrySet()) {
                builder.append("Seksjon: " + codelistsByOwner.getKey() + ", har antall kodelister: " + codelistsByOwner
                        .getValue()
                        .size() + "\n");
                codelistsByOwner.getValue().sort((c1, c2) -> Integer.compare(c1.size(), c2.size()));
                for (Codelist codelist : codelistsByOwner.getValue()) {
                    builder.append(codelist.toString() + "\n");
                }
                builder.append("\n\n\n");
            }
            log.info(builder.toString());
            return this;
        }

        public Map<String, List<Codelist>> indexByOwner() {
            return codelists.parallelStream().collect(groupingBy(codelist -> codelist.getOwner(), mapping(
                    codelist -> codelist, toList())));
        }

        @SuppressWarnings("unused")
        public CodelistCollection printCodelistsWithNumberOfCodes(int numberOfCodes) {
            List<Codelist> codelists = index().get(numberOfCodes);
            log.info("Printing codelists with number of codes: " + numberOfCodes);
            codelists.stream().forEach(System.out::println);
            return this;
        }

        public CodelistCollection printMostUsedCodes() {
            log.info("Print most used codes");
            List<Code> allCodes = codelists.stream().flatMap(codelist -> codelist.codes.stream()).collect(toList());
            Map<String, Long> grouped = allCodes.stream().collect(groupingBy(code -> code.title, counting()));
            grouped.entrySet().stream().sorted(Map.Entry.<String, Long> comparingByValue().reversed()).limit(100)
                    .forEach(System.out::println); // or any other terminal method
            log.info("Print most used codes end");
            return this;
        }

        public static CodelistCollection toCodelists(List<Code> allcodes, List<Metadata> metadatas) {
            Map<String, List<Code>> codeMap = allcodes.parallelStream().collect(groupingBy(code -> code.codelistId,
                    mapping(code -> code, toList())));
            List<Codelist> codelists = new ArrayList<>();
            for (Entry<String, List<Code>> codesEntry : codeMap.entrySet()) {
                Metadata metadataMatch = metadatas.parallelStream().filter(metadata -> metadata.codelistId.equals(
                        codesEntry
                                .getKey())).findAny().get();
                codelists.add(new Codelist(codesEntry.getValue(), metadataMatch));
            }
            return new CodelistCollection(codelists);
        }

        @SuppressWarnings("unused")
        public CodelistCollection printCodelistsContainingCodeWithTitle(String title) {
            codelists.parallelStream().filter(codelist -> codelist.hasCodeWithTitle(title)).collect(toList()).forEach(
                    codelist -> log.info("Containing " + title + ". " + codelist));
            return this;
        }

        private Map<Integer, List<Codelist>> index() {
            return new TreeMap<>(codelists.parallelStream().collect(groupingBy(
                    codelist -> codelist.size(), mapping(codelist -> codelist, toList()))));
        }

        public CodelistCollection printCount(String prefix) {
            log.info(prefix + " number of codelists: " + codelists.size());
            return this;
        }

        public CodelistCollection printDistribution() {
            StringBuilder builder = new StringBuilder();
            for (Entry<Integer, List<Codelist>> entry : index().entrySet()) {
                builder.append(entry.getKey() + " codes in " + entry.getValue().size() + " codelists\n");
            }
            log.info("Distribution: \n" + builder.toString());
            return this;
        }

        private CodelistCollection removeCodelists(List<Codelist> remove) {
            List<Codelist> tmp = Lists.newArrayList(codelists);
            tmp.removeAll(remove);
            return new CodelistCollection(tmp);
        }

        public CodelistCollection removeSingleCodes() {
            List<Codelist> singleCodes = codelists.parallelStream().filter(codelist -> codelist.size() < 2).collect(
                    toList());
            log.info("Removing codelists with single code: " + singleCodes.size());
            return removeCodelists(singleCodes);
        }

        public CodelistCollection removeDuplicates() {
            Set<Codelist> uniqueCodelists = new HashSet<>(codelists);
            log.info("Removing duplicated codelists: " + (codelists.size() - uniqueCodelists.size()));
            return new CodelistCollection(uniqueCodelists);
        }

        public CodelistCollection removeCodelistWithCodeTitle(String title) {
            List<Codelist> removed = codelists.parallelStream().filter(codelist -> codelist.hasCodeWithTitle(title))
                    .collect(toList());
            log.info("Removing codelists having code with title '" + title + "':" + removed.size());
            return removeCodelists(removed);
        }

        public CodelistCollection removeCodelistsWithNumericCodeTitles() {
            List<Codelist> removed = codelists.parallelStream().filter(codelist -> codelist.hasNumericCodeTitles())
                    .collect(toList());
            log.info("Removing codelists with numeric code titles: " + removed.size());
            return removeCodelists(removed);
        }

        @SuppressWarnings("unused")
        public CodelistCollection removeCodelistsWithNonNumericCodes() {
            List<Codelist> removed = codelists.parallelStream().filter(codelist -> codelist.hasNonNumericCodes())
                    .collect(
                            toList());
            log.info("Removing codelists with non numeric codes: " + removed.size());
            return removeCodelists(removed);
        }

        public CodelistCollection removeExtracts() {
            List<Codelist> removed = codelists.parallelStream().filter(codelist -> isExtractOfOther(codelist)).collect(
                    toList());
            log.info("Removing extract codelists: " + removed.size());
            return removeCodelists(removed);
        }

        public CodelistCollection removeAlmostExtracts() {
            List<Codelist> removed = codelists.parallelStream().filter(codelist -> isAlmostExtractOfOther(codelist))
                    .collect(toList());
            log.info("Removing codelist that has half or more codes matching another codelists: " + removed.size());
            return removeCodelists(removed);
        }

        public CodelistCollection removeStabasExtracts(List<Codelist> stabasCodelists) {
            List<Codelist> removed = codelists.parallelStream().filter(codelist -> isExtractOfStabasCodelists(codelist,
                    stabasCodelists)).collect(toList());
            log.info("Removing codelist that is extract from stabas: " + removed.size());
            return removeCodelists(removed);
        }

        private boolean isExtractOfStabasCodelists(Codelist codelist, List<Codelist> stabasCodelists) {
            return stabasCodelists.parallelStream().anyMatch(stabasCodelist -> stabasCodelist.containsAll(
                    codelist.codes));
        }

        private boolean isMostlyExtractOfStabasCodelists(Codelist codelist, List<Codelist> stabasCodelists) {
            return stabasCodelists.parallelStream().anyMatch(stabasCodelist -> stabasCodelist.containsMost(
                    codelist.codes));
        }

        public CodelistCollection removeAlmostStabasExtracts(List<Codelist> stabasCodelists) {
            List<Codelist> removed = codelists.parallelStream().filter(codelist -> isMostlyExtractOfStabasCodelists(
                    codelist, stabasCodelists)).collect(toList());
            log.info("Removing codelist that has half or more codes matching a stabas classification: " + removed
                    .size());
            return removeCodelists(removed);
        }

        private boolean isExtractOfOther(Codelist codelist) {
            return findCodelistsWithEqualOrMoreCodesButNotSelf(codelist.size(), codelist).parallelStream().anyMatch(
                    testCodelist -> testCodelist
                            .containsAll(codelist.codes));
        }

        private boolean isAlmostExtractOfOther(Codelist codelist) {
            return findCodelistsWithEqualOrMoreCodesButNotSelf(codelist.size(), codelist).parallelStream()
                    .anyMatch(testCodelist -> testCodelist.containsMost(codelist.codes));
        }

        private List<Codelist> findCodelistsWithEqualOrMoreCodesButNotSelf(int countCodes, Codelist self) {
            return codelists.parallelStream()
                    .filter(codelist -> codelist.size() >= countCodes)
                    .filter(codelist -> codelist != self)
                    .collect(toList());
        }
    }

    public static class Codelist {
        private final String codelistName;
        private final String generationName;
        private final String filklasseName;
        private final String filklasseOwner;
        private final String substammeName;
        private final String substammeOwner;
        private final String stammeName;
        private final String stammeOwner;
        private final List<Code> codes;
        private static final List<String> SENTINEL;

        static {
            SENTINEL = Lists.newArrayList("vet ikke",
                    "uoppgitt",
                    "vil ikke svare",
                    "refusal",
                    "dont know",
                    "no answer",
                    "nekter",
                    "not applicable",
                    "uaktuelt",
                    "not available",
                    "ikke besvart",
                    "not stated",
                    "don't know",
                    "husker ikke",
                    "andre grunner",
                    "refused",
                    "nekter å svare",
                    "ukjent",
                    "ikke relevant",
                    "dont know, never saw r, no selected r",
                    "upers",
                    "don´t know",
                    "ikke relevant",
                    "uoppgitt/nekt",
                    "ikke svare",
                    "ubesvart",
                    "blank",
                    "io ønsker ikke å delta",
                    "io er langvarig syk",
                    "io er kortvarig syk",
                    "io ikke å treffe av andre årsaker",
                    "io er død",
                    "andre nekter for io",
                    "språkproblemer",
                    "vet ikke/vil ikke svare",
                    "kan ikke svare",
                    "io selv",
                    "not answered");
        }

        Codelist(List<Code> codes, Metadata metadata) {
            checkNotNull(metadata);
            this.codelistName = metadata.codelistName;
            this.generationName = metadata.generationName;
            this.filklasseName = metadata.filklasseName;
            this.filklasseOwner = Strings.nullToEmpty(metadata.filklasseOwner).trim();
            this.substammeName = metadata.substammeName;
            this.substammeOwner = Strings.nullToEmpty(metadata.substammeOwner).trim();
            this.stammeName = metadata.stammeName;
            this.stammeOwner = Strings.nullToEmpty(metadata.stammeOwner).trim();
            this.codes = codes.parallelStream().map(code -> new Code(code)).filter(code -> !isSentinel(code)).filter(
                    code -> !code.title.trim().isEmpty()).collect(toSet()).parallelStream().sorted().collect(toList());
        }

        public boolean hasNonNumericCodes() {
            return codes.parallelStream().anyMatch(code -> !StringUtils.isNumeric(code.code));
        }

        public boolean hasNumericCodeTitles() {
            return codes.parallelStream().anyMatch(code -> StringUtils.isNumeric(code.title));
        }

        public boolean containsAll(List<Code> codes) {
            for (Code code : codes) {
                if (!hasCodeWithTitle(code.title)) {
                    return false;
                }
            }
            return true;
        }

        private boolean isSentinel(Code code) {
            return SENTINEL.contains(code.title);
        }

        public boolean containsMost(List<Code> codes) {
            int notFoundCount = 0;
            int foundCount = 0;
            for (Code code : codes) {
                if (!hasCodeWithTitle(code.title)) {
                    notFoundCount++;
                } else {
                    foundCount++;
                }
            }
            return foundCount >= notFoundCount;
        }

        public boolean isAllTitlesEqual() {
            return codes.parallelStream().map(code -> code.title).collect(toSet()).size() == 1;
        }

        public int size() {
            return codes.size();
        }

        @Override
        public int hashCode() {
            return 1;

        }

        @Override
        public String toString() {
            return stammeName + ":" + substammeName + ":" + filklasseName + ":" + generationName + ":" + codelistName
                    + " ^ antall koder: " + codes.size() + " ^ " + codes;
        }

        public boolean hasCodeWithTitle(String title) {
            return codes.parallelStream().filter(code -> code.title.equals(title)).collect(toSet()).size() > 0;
        }

        public String getOwner() {
            if (!filklasseOwner.isEmpty()) {
                return filklasseOwner;
            }
            if (!substammeOwner.isEmpty()) {
                return substammeOwner;
            }
            if (!stammeOwner.isEmpty()) {
                return stammeOwner;
            }
            throw new IllegalStateException("Has no owner: " + this);
        }

        @Override
        public boolean equals(Object o) {
            Codelist other = (Codelist) o;
            if (other.size() != size()) {
                return false;
            }
            return codes.containsAll(other.codes);
        }
    }

    @JsonPropertyOrder({ "VARIABEL_ID", "KODE_VERDI", "KODE_NAVN" })
    public static class Code implements Comparable<Code> {
        @JsonProperty("VARIABEL_ID")
        private String codelistId;
        @JsonProperty("KODE_VERDI")
        private String code;
        @JsonProperty("KODE_NAVN")
        private String title;

        protected Code() {
        }

        Code(String codelistId, String code, String title) {
            this.codelistId = codelistId;
            this.code = code.toLowerCase().trim();
            this.title = title.toLowerCase().trim();
        }

        Code(Code code) {
            this(code.codelistId, code.code, code.title);
        }

        @Override
        public String toString() {
            return code + "-" + title;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(title);
        }

        @Override
        public boolean equals(Object obj) {
            Code other = (Code) obj;
            return Objects.equal(title, other.title);
            // return Objects.equal(code, other.code) && Objects.equal(title, other.title);
        }

        @Override
        public int compareTo(Code o) {
            return code.compareTo(o.code);
        }
    }

    @JsonPropertyOrder({ "VARIABEL_ID", "VARIABEL_NAME", "GENERASJON", "FILKLASSE", "FILEIER", "SUBSTAMME",
            "SUBSTAMME_EIER", "STAMME", "STAMME_EIER" })
    public static class Metadata {
        @JsonProperty("VARIABEL_ID")
        private String codelistId;

        @JsonProperty("VARIABEL_NAME")
        private String codelistName;

        @JsonProperty("GENERASJON")
        private String generationName;

        @JsonProperty("FILKLASSE")
        private String filklasseName;

        @JsonProperty("FILEIER")
        private String filklasseOwner;

        @JsonProperty("SUBSTAMME")
        private String substammeName;

        @JsonProperty("SUBSTAMME_EIER")
        private String substammeOwner;

        @JsonProperty("STAMME")
        private String stammeName;

        @JsonProperty("STAMME_EIER")
        private String stammeOwner;

    }
}
