package no.ssb.klass.initializer.stabas;

import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

/**
 * Codelists fetched from stabas web service does not include shortname for classification items. And is also missing
 * include/include_also/excludes. This class patches classificationItems with missing shortname, include, include_also
 * and excludes.
 */
class ShortnamePatcher {
    private final List<PatchRow> patches;
    private final TranslatablePersistenceConverter translatableConverter;

    ShortnamePatcher(StabasConfiguration stabasConfiguration) {
        this.patches = readPatchFile(StabasUtils.openFile(stabasConfiguration.getPatchfile()));
        this.translatableConverter = StabasUtils.createTranslatableConverter();
    }

    public void patch(String versionId, ClassificationVersionWrapper version) {
        for (PatchRow patch : findPatchesForVersion(versionId)) {
            ConcreteClassificationItem item = (ConcreteClassificationItem) version.findItemWithStabasId(patch
                    .getItemId());
            patchShortTitle(item, toTranslatable(patch.getShortTitle()));
            patchNotes(item, toTranslatable(patch.getIncludes()), toTranslatable(patch.getIncludesAlso()),
                    toTranslatable(patch.getExcludes()));
        }
    }

    private List<PatchRow> readPatchFile(File patchfile) {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(PatchRow.class).withHeader();
        ObjectReader reader = mapper.readerFor(PatchRow.class).with(schema)
                .withoutRootName();
        try {
            MappingIterator<PatchRow> iterator = reader.readValues(patchfile);
            return iterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<PatchRow> findPatchesForVersion(String stabasVersionId) {
        return patches.stream().filter(patch -> patch.getVersionId().equals(stabasVersionId)).collect(toList());
    }

    private void patchNotes(ConcreteClassificationItem item, Translatable includes, Translatable includesAlso,
            Translatable excludes) {
        String notesNo = patchNotes(Language.NB, item, includes, "Inkluderer: ", includesAlso, "Inkluderer også: ",
                excludes, "Ekskluderer: ");
        String notesNn = patchNotes(Language.NN, item, includes, "Inkluderer: ", includesAlso, "Inkluderer også: ",
                excludes, "Ekskluderer: ");
        String notesEn = patchNotes(Language.EN, item, includes, "includes: ", includesAlso, "Includes also: ",
                excludes, "Excludes: ");
        item.setNotes(notesNo, Language.NB);
        item.setNotes(notesNn, Language.NN);
        item.setNotes(notesEn, Language.EN);
    }

    private String patchNotes(Language language, ClassificationItem item, Translatable includes,
            String includesPrefix, Translatable includesAlso, String includesAlsoPrefix,
            Translatable excludes, String excludesPrefix) {
        List<String> parts = new ArrayList<>();
        if (hasLanguage(includes, language)) {
            parts.add(includesPrefix + includes.getString(language).trim());
        }

        if (hasLanguage(includesAlso, language)) {
            parts.add(includesAlsoPrefix + includesAlso.getString(language).trim());
        }

        if (hasLanguage(excludes, language)) {
            parts.add(excludesPrefix + excludes.getString(language).trim());
        }

        String currentNotes = item.getNotes(language);
        if (!Strings.isNullOrEmpty(currentNotes)) {
            parts.add(currentNotes);
        }
        return Joiner.on("\n").join(parts);
    }

    private boolean hasLanguage(Translatable translatable, Language language) {
        String languageString = translatable.getString(language).trim();
        return !Strings.isNullOrEmpty(languageString);
    }

    private void patchShortTitle(ConcreteClassificationItem item, Translatable shortTitle) {
        item.setShortName(shortTitle.getString(Language.NB), Language.NB);
        item.setShortName(shortTitle.getString(Language.NN), Language.NN);
        item.setShortName(shortTitle.getString(Language.EN), Language.EN);
    }

    private Translatable toTranslatable(String text) {
        return translatableConverter.convertToEntityAttribute(text);
    }

    @JsonPropertyOrder({ "VERSION_ID", "LEVEL_ID", "ITEM_ID", "SHORT_TITLE", "INCLUDES", "INCLUDES_ALSO", "EXCLUDES" })
    private static class PatchRow {
        @JsonProperty("VERSION_ID")
        private String versionId;
        @JsonProperty("LEVEL_ID")
        private String levelId;
        @JsonProperty("ITEM_ID")
        private String itemId;
        @JsonProperty("SHORT_TITLE")
        private String shortTitle;
        @JsonProperty("INCLUDES")
        private String includes;
        @JsonProperty("INCLUDES_ALSO")
        private String includesAlso;
        @JsonProperty("EXCLUDES")
        private String excludes;

        public String getVersionId() {
            return versionId;
        }

        public String getItemId() {
            return levelId + "-" + itemId;
        }

        public String getShortTitle() {
            return shortTitle;
        }

        public String getIncludes() {
            return includes;
        }

        public String getIncludesAlso() {
            return includesAlso;
        }

        public String getExcludes() {
            return excludes;
        }
    }
}
