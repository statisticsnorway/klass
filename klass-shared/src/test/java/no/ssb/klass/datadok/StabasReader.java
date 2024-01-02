package no.ssb.klass.datadok;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.datadok.DatadokTest.Code;
import no.ssb.klass.datadok.DatadokTest.Codelist;
import no.ssb.klass.datadok.DatadokTest.Metadata;
import no.ssb.klass.initializer.stabas.StabasConfiguration;
import no.ssb.klass.initializer.stabas.StabasUtils;
import no.ssb.ns.meta.classification.ClassificationType;
import no.ssb.ns.meta.classification.ClassificationVersionType;
import no.ssb.ns.meta.classification.LevelType;
import no.ssb.ns.meta.classification.ObjectFactory;
import no.ssb.ns.meta.codelist.CodeType;
import no.ssb.ns.meta.codelist.CodelistType;

public class StabasReader {
    private static final Logger log = LoggerFactory.getLogger(StabasReader.class);
    private final Unmarshaller jaxbUnmarshaller;

    public StabasReader() throws Exception {
        this.jaxbUnmarshaller = JAXBContext.newInstance(ObjectFactory.class).createUnmarshaller();
    }

    public List<Codelist> readStabas(Language language) throws Exception {
        List<Codelist> codelists = new ArrayList<>();
        try (ZipFile zippedClassifications = new ZipFile(new StabasConfiguration().getClassificationsZipFile())) {
            for (ZipEntry classificationEntry : StabasUtils.listEntriesInZip(zippedClassifications)) {
                ClassificationType stabasClassification = unmarshallClassification(StabasUtils.getInputStream(
                        zippedClassifications, classificationEntry));

                if (!hasVersions(stabasClassification)) {
                    String title = StabasUtils.getStringNo(stabasClassification.getTitleGrp().getTitle());
                    log.warn("Skipping classification with no versions: " + title);
                    continue;
                }
                codelists.add(createCodelist(getLastVersion(filterVersions(stabasClassification)), language));
            }
        }
        return codelists;
    }

    private ClassificationVersionType getLastVersion(List<ClassificationVersionType> versions) {
        ClassificationVersionType lastVersion = versions.get(0);
        for (ClassificationVersionType version : versions) {
            if (toLocalDate(version.getValidFrom()).isAfter(toLocalDate(lastVersion.getValidFrom()))) {
                lastVersion = version;
            }
        }
        return lastVersion;
    }

    private LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        return calendar == null ? LocalDate.MIN : calendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private Codelist createCodelist(ClassificationVersionType stabasVersion, Language language) throws IOException {
        List<Code> codes = new ArrayList<>();
        LevelType stabasLevel = stabasVersion.getLevel();
        try (ZipFile zippedCodelists = new ZipFile(new StabasConfiguration().getCodelistsZipFile())) {
            while (stabasLevel != null) {
                codes.addAll(createCodes(stabasLevel, language, zippedCodelists));
                stabasLevel = stabasLevel.getLevel();
            }
        }
        return new Codelist(codes, new Metadata());
    }

    private List<Code> createCodes(LevelType stabasLevel, Language language, ZipFile zippedCodelists) {
        List<Code> codes = new ArrayList<>();
        String codelistFilename = StabasUtils.parseUrn(stabasLevel.getId()) + ".xml";
        log.info("Importing codelist: " + codelistFilename);
        CodelistType stabasCodelist = unmarshallCodelist(StabasUtils.getInputStream(zippedCodelists, zippedCodelists
                .getEntry(codelistFilename)));
        for (CodeType code : stabasCodelist.getCodes().getCode()) {
            String codeValue = code.getCodeValue();
            Translatable officialName = StabasUtils.createTranslatable(code.getCodeTextGrp().getCodeText());
            codes.add(new Code("xx", codeValue, officialName.getString(language)));
        }
        return codes;
    }

    private CodelistType unmarshallCodelist(InputStream inputStream) {
        try {
            return (CodelistType) jaxbUnmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to unmarshall codelist");
        }
    }

    @SuppressWarnings("unchecked")
    private ClassificationType unmarshallClassification(InputStream inputStream) throws JAXBException {
        JAXBElement<ClassificationType> jaxbElement = (JAXBElement<ClassificationType>) jaxbUnmarshaller.unmarshal(
                inputStream);
        return jaxbElement.getValue();
    }

    private boolean hasVersions(ClassificationType stabasClassification) {
        return filterVersions(stabasClassification).size() != 0;
    }

    private List<ClassificationVersionType> filterVersions(ClassificationType stabasClassification) {
        return stabasClassification.getClassificationVersions().getClassificationVersion().stream().filter(
                version -> version.isShowInternet() || version.isShowIntranet()).collect(toList());
    }
}
