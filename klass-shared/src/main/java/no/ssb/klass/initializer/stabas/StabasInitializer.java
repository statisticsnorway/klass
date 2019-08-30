package no.ssb.klass.initializer.stabas;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.MigratedFrom;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.initializer.stabas.ClassificationVersionWrapper.ClassificationItemWrapper;
import no.ssb.ns.meta.classification.ClassificationType;
import no.ssb.ns.meta.classification.ClassificationVersionType;
import no.ssb.ns.meta.classification.ClassificationVersionType.ChangesGrp;
import no.ssb.ns.meta.classification.DescriptionGrp;
import no.ssb.ns.meta.classification.LevelType;
import no.ssb.ns.meta.classification.ObjectFactory;
import no.ssb.ns.meta.codelist.CodeType;
import no.ssb.ns.meta.codelist.Codelist;
import no.ssb.ns.meta.codelist.CodelistType;
import no.ssb.ns.meta.common.PersonType;
import no.ssb.ns.meta.common.StringLangType;

/**
 * Populates database with classifications from stabas.
 */
@Service
public class StabasInitializer {
    private static final Logger log = LoggerFactory.getLogger(StabasInitializer.class);
    private final ClassificationService classificationService;
    private final UserService userService;
    private final StabasConfiguration stabasConfiguration;
    private final Unmarshaller jaxbUnmarshaller;
    private StatisticalUnitsMapper statisticalUnitsMapper;
    private ClassificationFamilyMapper classificationFamilyMapper;
    private ClassificationNameMapper classificationNameMapper;
    private EntityManager entityManager;

    @Autowired
    public StabasInitializer(ClassificationService classificationService, UserService userService,
            StabasConfiguration stabasConfiguration, EntityManager entityManager) throws Exception {
        this.classificationService = classificationService;
        this.userService = userService;
        this.stabasConfiguration = stabasConfiguration;
        this.jaxbUnmarshaller = JAXBContext.newInstance(ObjectFactory.class).createUnmarshaller();
        this.entityManager = entityManager;
    }

    @Transactional
    public void run() throws Exception {
        // Only populating if no classifications from stabas already stored
        if (classificationService.countClassifications(MigratedFrom.STABAS) == 0) {
            Date start = TimeUtil.now();
            CorrespondenceTableInitilizer correspondenceTableInitilizer = new CorrespondenceTableInitilizer(
                    classificationService, stabasConfiguration);
            initMappers();
            populateClassifications(correspondenceTableInitilizer);
            populateCorrespondenceTables(correspondenceTableInitilizer);
            log.info("Importing from Stabas took(ms): " + TimeUtil.millisecondsSince(start));
        }
    }

    private void populateCorrespondenceTables(CorrespondenceTableInitilizer correspondenceTableInitilizer)
            throws IOException {
        correspondenceTableInitilizer.run();
    }

    private void populateClassifications(CorrespondenceTableInitilizer correspondenceTableInitilizer)
            throws IOException {
        ShortnamePatcher shortnamePatcher = new ShortnamePatcher(stabasConfiguration);
        try (ZipFile zippedClassifications = new ZipFile(StabasUtils.openFile(stabasConfiguration
                .getClassificationsZipFile()))) {
            for (ZipEntry classificationEntry : StabasUtils.listEntriesInZip(zippedClassifications)) {
                try {
                    log.info("Importing classification: " + classificationEntry.getName());
                    ClassificationType stabasClassification = unmarshallClassification(StabasUtils.getInputStream(
                            zippedClassifications, classificationEntry));
                    populateClassification(correspondenceTableInitilizer, shortnamePatcher, stabasClassification);
                } catch (Exception e) {
                    log.error("Failed importing classification", e);
                }
            }
        }
    }

    private void populateClassification(CorrespondenceTableInitilizer correspondenceTableInitilizer,
            ShortnamePatcher shortnamePatcher, ClassificationType stabasClassification) throws IOException {
        if (!hasVersions(stabasClassification)) {
            String title = StabasUtils.getStringNo(stabasClassification.getTitleGrp().getTitle());
            log.warn("Skipping classification with no versions: " + title);
            return;
        }
        ClassificationSeries classification = createClassification(correspondenceTableInitilizer, shortnamePatcher,
                stabasClassification);
        classificationService.saveNotIndexClassification(classification);
        entityManager.flush();
        entityManager.clear();
    }

    private ClassificationSeries createClassification(CorrespondenceTableInitilizer correspondenceTableInitilizer,
            ShortnamePatcher shortnamePatcher, ClassificationType stabasClassification) throws IOException {
        ClassificationSeries classification = createClassification(stabasClassification);
        for (ClassificationVersionType stabasClassificationVersion : filterVersions(stabasClassification)) {
            ClassificationVersionWrapper version = createClassificationVersion(stabasClassificationVersion,
                    shortnamePatcher);
            classification.addClassificationVersion(version.getVersion());
            correspondenceTableInitilizer.preparedVersion(version);
        }
        updateIncludeNotes(classification);
        updateIncludeShortName(classification);
        return classification;
    }

    private boolean hasVersions(ClassificationType stabasClassification) {
        return filterVersions(stabasClassification).size() != 0;
    }

    private List<ClassificationVersionType> filterVersions(ClassificationType stabasClassification) {
        return stabasClassification.getClassificationVersions().getClassificationVersion().stream()
                .filter(version -> version.isShowInternet() || version.isShowIntranet())
                .filter(version -> !VariantFilter.isVariant(StabasUtils.parseUrn(version.getId())))
                .collect(toList());
    }

    private void updateIncludeShortName(ClassificationSeries classification) {
        for (ClassificationVersion version : classification.getClassificationVersions()) {
            if (version.hasShortNames()) {
                classification.setIncludeShortName(true);
                return;
            }
        }
    }

    private void updateIncludeNotes(ClassificationSeries classification) {
        for (ClassificationVersion version : classification.getClassificationVersions()) {
            if (version.hasNotes()) {
                classification.setIncludeNotes(true);
                return;
            }
        }
    }

    private void initMappers() {
        statisticalUnitsMapper = new StatisticalUnitsMapper(classificationService.findAllStatisticalUnits());
        classificationFamilyMapper = new ClassificationFamilyMapper(classificationService
                .findAllClassificationFamilies());
        classificationNameMapper = new ClassificationNameMapper();
    }

    private ClassificationSeries createClassification(ClassificationType stabasClassification) {
        Translatable name = StabasUtils.createTranslatable(stabasClassification.getTitleGrp().getTitle());
        name = classificationNameMapper.mapClassificationName(name);
        Translatable description = StabasUtils.createTranslatable(stabasClassification.getDescriptionGrp()
                .getDescription());
        if (description.isEmpty()) {
            description = description.withLanguage("manglet beskrivelse i stabas", Language.NB);
        }

        ClassificationSeries classification = new ClassificationSeries(name, description, false, Language.NB,
                no.ssb.klass.core.model.ClassificationType.CLASSIFICATION, getOrCreateContactPerson(
                        stabasClassification));

        List<StatisticalUnit> statisticalUnits = getStatisticalUnits(stabasClassification.getStatisticalUnitGrp()
                .getStatisticalUnit());
        classification.getStatisticalUnits().addAll(statisticalUnits);
        classificationFamilyMapper.getClassificationFamily(stabasClassification.getSubjectArea(), StabasUtils
                .getStringNo(stabasClassification.getTitleGrp().getTitle())).addClassificationSeries(classification);
        classification.updateMigratedFrom(MigratedFrom.STABAS, Long.valueOf(StabasUtils.parseUrn(stabasClassification
                .getId())));
        return classification;
    }

    private ClassificationVersionWrapper createClassificationVersion(ClassificationVersionType stabasVersion,
            ShortnamePatcher shortnamePatcher) throws IOException {
        DateRange dateRange = createDateRange(stabasVersion.getValidFrom(), stabasVersion.getValidTo());
        Translatable introduction = createClassificationVersionIntroduction(stabasVersion.getDescriptionGrp(),
                stabasVersion.getChangesGrp());
        Translatable legalBase = Translatable.empty();
        if (stabasVersion.getLegalBaseGrp() != null && !isNotRelevant(stabasVersion.getLegalBaseGrp().getLegalBase())) {
            legalBase = StabasUtils.createTranslatable(stabasVersion.getLegalBaseGrp().getLegalBase());
        }
        Translatable publications = Translatable.empty();
        if (stabasVersion.getExternalDocumentGrp() != null && !isNotRelevant(stabasVersion.getExternalDocumentGrp()
                .getExternalDocument())) {
            publications = StabasUtils.createTranslatable(stabasVersion.getExternalDocumentGrp().getExternalDocument());
        }
        Translatable derivedFrom = Translatable.empty();
        if (stabasVersion.getDerivedFromGrp() != null && !isNotRelevant(stabasVersion.getDerivedFromGrp()
                .getDerviedFrom())) {
            derivedFrom = StabasUtils.createTranslatable(stabasVersion.getDerivedFromGrp().getDerviedFrom());
        }
        ClassificationVersion version = new ClassificationVersion(dateRange, introduction, legalBase, publications,
                derivedFrom);

        ClassificationVersionWrapper versionWrapper = new ClassificationVersionWrapper(StabasUtils.parseUrn(
                stabasVersion.getId()), version);
        LevelType stabasLevel = stabasVersion.getLevel();
        try (ZipFile zippedCodelists = new ZipFile(StabasUtils.openFile(stabasConfiguration.getCodelistsZipFile()))) {
            while (stabasLevel != null) {
                Level level = createLevel(stabasLevel);
                versionWrapper.addLevel(StabasUtils.parseUrn(stabasLevel.getId()), level);
                versionWrapper.addClassificationItems(createClassificationItems(stabasLevel, zippedCodelists));
                stabasLevel = stabasLevel.getLevel();
            }
        }
        versionWrapper.patch(shortnamePatcher);
        versionWrapper.publish();

        return versionWrapper;
    }

    private Translatable createClassificationVersionIntroduction(DescriptionGrp descriptionGrp,
            ChangesGrp changesGrp) {
        List<StringLangType> description = (descriptionGrp == null) ? null : descriptionGrp.getDescription();
        List<StringLangType> changes = null;
        if (changesGrp != null && !isNotRelevant(changesGrp.getChange())) {
            changes = changesGrp.getChange();
        }

        String introductionNo = createClassificationVersionIntroduction(
                StabasUtils.getStringNo(description), StabasUtils.getStringNo(changes));
        String introductionNn = createClassificationVersionIntroduction(
                StabasUtils.getStringNn(description), StabasUtils.getStringNn(changes));
        String introductionEn = createClassificationVersionIntroduction(
                StabasUtils.getStringEn(description), StabasUtils.getStringEn(changes));

        return StabasUtils.createTranslatable(introductionNo, introductionNn, introductionEn);
    }

    private boolean isNotRelevant(List<StringLangType> text) {
        return StringUtils.equalsIgnoreCase("ikke relevant", StringUtils.trim(StabasUtils.getStringNo(text)));
    }

    private String createClassificationVersionIntroduction(String description, String changes) {
        description = (description == null) ? description : description.trim();
        changes = (changes == null) ? changes : changes.trim();
        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(description)) {
            builder.append(description);
            if (!Strings.isNullOrEmpty(changes)) {
                builder.append("\n\n");
            }
        }
        if (!Strings.isNullOrEmpty(changes)) {
            builder.append(changes);
        }
        return builder.toString();
    }

    private List<ClassificationItemWrapper> createClassificationItems(LevelType stabasLevel, ZipFile zippedCodelists) {
        List<ClassificationItemWrapper> classificationItems = new ArrayList<>();
        String codelistFilename = StabasUtils.parseUrn(stabasLevel.getId()) + ".xml";
        log.info("Importing codelist: " + codelistFilename);
        CodelistType stabasCodelist = unmarshallCodelist(StabasUtils.getInputStream(zippedCodelists, zippedCodelists
                .getEntry(codelistFilename)));
        for (CodeType code : stabasCodelist.getCodes().getCode()) {
            String codeValue = code.getCodeValue();
            Translatable officialName = StabasUtils.createTranslatable(code.getCodeTextGrp().getCodeText());
            Translatable shortName = Translatable.empty();
            Translatable notes = code.getDescriptionGrp() == null ? Translatable.empty()
                    : StabasUtils.createTranslatable(code.getDescriptionGrp().getDescription());
            ConcreteClassificationItem classificationItem = new ConcreteClassificationItem(codeValue, officialName,
                    shortName, notes);
            classificationItems.add(new ClassificationItemWrapper(StabasUtils.parseUrn(code.getId()),
                    classificationItem, stabasLevel.getLevelNumber(), StabasUtils.parseUrn(code
                            .getParentCodeId())));
        }
        return classificationItems;
    }

    private CodelistType unmarshallCodelist(InputStream inputStream) {
        try {
            return (Codelist) jaxbUnmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to unmarshall codelist");
        }
    }

    private Level createLevel(LevelType stabasLevel) {
        Translatable levelName = StabasUtils.createTranslatable(removeVersionFromLevelName(stabasLevel.getTitleGrp()
                .getTitle()));
        Level level = new Level(stabasLevel.getLevelNumber(), levelName);
        return level;
    }

    private List<StringLangType> removeVersionFromLevelName(List<StringLangType> originalLevelNames) {
        List<StringLangType> levelNames = new ArrayList<>();
        for (StringLangType originalLevelName : originalLevelNames) {
            StringLangType levelName = new StringLangType();
            levelName.setLang(originalLevelName.getLang());
            // From Stabas web service level name is 'version_name: level_name'. Removing version name
            levelName.setValue(originalLevelName.getValue().substring(originalLevelName.getValue().indexOf(": ") + 2));
            levelNames.add(levelName);
        }
        return levelNames;
    }

    private DateRange createDateRange(XMLGregorianCalendar from, XMLGregorianCalendar to) {
        LocalDate toDate = toLocalDate(to);
        if (toDate != null) {
            toDate = toDate.plusDays(1);
        }
        return DateRange.create(toLocalDate(from), toDate);
    }

    private LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        return calendar == null ? null : calendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private List<StatisticalUnit> getStatisticalUnits(List<StringLangType> translations) {
        return statisticalUnitsMapper.getStatisticalUnits(StabasUtils.getStringNo(translations));
    }

    @SuppressWarnings("unchecked")
    private ClassificationType unmarshallClassification(InputStream inputStream) throws JAXBException {
        JAXBElement<ClassificationType> jaxbElement = (JAXBElement<ClassificationType>) jaxbUnmarshaller.unmarshal(
                inputStream);
        return jaxbElement.getValue();
    }

    private User getOrCreateContactPerson(ClassificationType stabasClassification) {
        PersonType person = getContactPersonFromCurrentVersion(stabasClassification);
        String username = StringUtils.trim(person.getPerson());
        User user = userService.getUserByUserName(username);
        if (user == null) {
            user = new User(username, username, StringUtils.trim(person.getDivision()));
            userService.saveUser(user);
        }
        return user;
    }

    private PersonType getContactPersonFromCurrentVersion(ClassificationType stabasClassification) {
        return stabasClassification.getClassificationVersions().getClassificationVersion().stream()
                .filter(version -> version.isIsCurrentVersion())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No current version found"))
                .getContactInformation();
    }
}
