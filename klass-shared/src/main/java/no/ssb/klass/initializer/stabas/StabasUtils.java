package no.ssb.klass.initializer.stabas;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.base.Strings;

import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.ns.meta.common.StringLangType;

public final class StabasUtils {
    private static final String STABAS_EN_LANGUAGE = "en";
    private static final String STABAS_NN_LANGUAGE = "nn";
    private static final String STABAS_NO_LANGUAGE = "nb";

    private StabasUtils() {
        // Utility class
    }

    private static File copyToTempDir(String filePath) throws IOException {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File dest = new File(baseDir + "/" + new File(filePath).getName());
        InputStream resourceAsStream = StabasUtils.class.getClassLoader().getResourceAsStream(filePath);
        FileUtils.copyInputStreamToFile(resourceAsStream, dest);
        return dest;
    }

    public static File openFile(String filename) {
        try {
            return copyToTempDir(filename);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open: " + filename, e);
        }
    }

    public static TranslatablePersistenceConverter createTranslatableConverter() {
        return new TranslatablePersistenceConverter(STABAS_NO_LANGUAGE, STABAS_NN_LANGUAGE, STABAS_EN_LANGUAGE);
    }

    public static String parseUrn(String urn) {
        if (urn == null) {
            return null;
        }
        return urn.substring(urn.lastIndexOf(':') + 1);
    }

    public static Translatable createTranslatable(List<StringLangType> translations) {
        return createTranslatable(getString(translations, StabasUtils.STABAS_NO_LANGUAGE), getString(translations,
                StabasUtils.STABAS_NN_LANGUAGE), getString(translations, StabasUtils.STABAS_EN_LANGUAGE));
    }

    public static Translatable createTranslatable(String no, String nn, String en) {
        return new Translatable(replaceGreaterThanAndSmallerThan(no), replaceGreaterThanAndSmallerThan(nn),
                replaceGreaterThanAndSmallerThan(en));
    }

    private static String getString(List<StringLangType> translations, String language) {
        if (translations == null) {
            return null;
        }

        for (StringLangType translation : translations) {
            if (translation.getLang().equals(language)) {
                if (translation.getValue() == null) {
                    return null;
                }
                return translation.getValue().trim();
            }
        }
        return null;
    }

    private static String replaceGreaterThanAndSmallerThan(String string) {
        if (Strings.isNullOrEmpty(string)) {
            return string;
        }
        return string
                .replace("&gt;", ">")
                .replace("&amp;gt;", ">")
                .replace("&lt;", "<")
                .replace("&It;", "<")
                .replace("&amp;It;", "<")
                .replace("&amp;lt;", "<");
    }

    public static String getStringNo(List<StringLangType> translations) {
        return getString(translations, STABAS_NO_LANGUAGE);
    }

    public static String getStringNn(List<StringLangType> translations) {
        return getString(translations, STABAS_NN_LANGUAGE);
    }

    public static String getStringEn(List<StringLangType> translations) {
        return getString(translations, STABAS_EN_LANGUAGE);
    }

    @SuppressWarnings("unchecked")
    public static List<ZipEntry> listEntriesInZip(ZipFile zipFile) {
        return (List<ZipEntry>) Collections.list(zipFile.entries());
    }

    public static InputStream getInputStream(ZipFile zipFile, ZipEntry zipEntry) {
        try {
            return zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
