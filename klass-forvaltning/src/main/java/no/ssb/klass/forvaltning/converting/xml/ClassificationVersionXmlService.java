package no.ssb.klass.forvaltning.converting.xml;

import static no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionContainer.*;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.forvaltning.converting.exception.VersionImportException;
import no.ssb.klass.forvaltning.converting.xml.abstracts.XmlCodeHierarchyService;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlCodeHierarchy;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionContainer;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionExportContainer;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;

/**
 *
 * Service that takes care of xml import and export for ClassificationVersion
 *
 * @author Mads Lundemo, SSB.
 */
@Service
@Profile(ConfigurationProfiles.FRONTEND_ONLY)
public class ClassificationVersionXmlService extends XmlCodeHierarchyService<ClassificationVersion, XmlVersionItem> {

    private static final Logger log = LoggerFactory.getLogger(ClassificationVersionXmlService.class);

    public InputStream toXmlStream(ClassificationVersion version) {
        XmlVersionExportContainer container = versionToDto(version);
        ObjectWriter writer = getObjectWriter(XmlVersionExportContainer.class);
        return createInputStream(container, writer);
    }

    public void fromXmlStreamAndMerge(InputStream stream, ClassificationVersion version) throws ImportException {
        List<XmlVersionItem> values = readInputStream(stream, XmlVersionItem.class);
        log.info("import file contains " + values.size() + " elements");
        checkForExistingCodes(version, values);
        checkForMissingTitles(values);
        checkForMissingCodes(values);
        checkForDuplicatedCodes(values);

        if (version.isIncludeValidity()) {
            checkForMissingDates(values);
        }
        if (version.isIncludeShortName()) {
            checkForMissingShortName(values, version.getPrimaryLanguage());
        }

        Map<ClassificationItem, ClassificationItem> itemMap =
                createClassificationItems(version, values.stream()
                        .map(versionItem -> (XmlCodeHierarchy) versionItem)
                        .collect(Collectors.toList()));

        mergeItemsWithClassification(version, itemMap);
    }

    private void checkForDuplicatedCodes(List<XmlVersionItem> values) throws ImportException {
        List<String> lookupTableCode = new ArrayList<>();
        for (XmlVersionItem versionItem : values) {
            String code = versionItem.getCode();
            if (code != null) {
                if (lookupTableCode.contains(code)) {
                    throw new ImportException(
                            "Alle elementer må ha en unik kode, følgende kode er brukt flere ganger : " + code);
                }
                lookupTableCode.add(code);
            }
        }
    }

    private void checkForMissingDates(List<XmlVersionItem> values) throws ImportException {
        for (XmlVersionItem versionItem : values) {
            if (!versionItem.isEmpty()
                    && Strings.isNullOrEmpty(versionItem.getValidFrom())) {
                throw new ImportException("Element med kode  '" + versionItem.getCode() + "' mangler gyldig fra dato");
            }
        }
    }

    private void checkForMissingShortName(List<XmlVersionItem> values, Language primary) throws ImportException {
        for (XmlVersionItem versionItem : values) {
            if (versionItem.isEmpty()) {
                continue;
            }
            String shortName;
            switch (primary) {
            case NB:
                shortName = versionItem.getShortNameNB();
                break;
            case NN:
                shortName = versionItem.getShortNameNN();
                break;
            case EN:
                shortName = versionItem.getShortNameEN();
                break;
            default:
                shortName = null;
            }
            if (Strings.isNullOrEmpty(shortName)) {
                throw new ImportException("Element med kode  '"
                        + versionItem.getCode() + "' mangler kortnavn på "
                        + primary.getDisplayName());
            }
        }

    }

    private void checkForMissingCodes(List<XmlVersionItem> values) throws ImportException {
        for (XmlVersionItem versionItem : values) {
            if (!versionItem.isEmpty() && Strings.isNullOrEmpty(versionItem.getCode())) {
                throw new ImportException("Ett eller flere elementer mangler kode");
            }
        }
    }

    private void checkForMissingTitles(List<XmlVersionItem> values) throws ImportException {
        for (XmlVersionItem versionItem : values) {
            if (!versionItem.isEmpty()
                    && Strings.isNullOrEmpty(versionItem.getNameNB())
                    && Strings.isNullOrEmpty(versionItem.getNameNN())
                    && Strings.isNullOrEmpty(versionItem.getNameEN())) {
                throw new ImportException("Element med kode " + versionItem.getCode() + " mangler tittel");
            }
        }
    }

    protected XmlVersionExportContainer versionToDto(ClassificationVersion version) {
        List<XmlVersionItem> list = version.getAllClassificationItems()
                .stream()
                .map(XmlVersionItem::new)
                .sorted(Comparator.comparing(XmlVersionItem::getCode))
                .collect(Collectors.toCollection(LinkedList::new));

        XmlVersionExportContainer container = new XmlVersionExportContainer(list);
        container.setSchemaBaseUrl(SchemaBaseUrl);
        return container;
    }

    private void checkForExistingCodes(ClassificationVersion version, List<XmlVersionItem> values)
            throws VersionImportException {
        List<XmlVersionItem> existing = values.stream()
                .filter(xmlVersionItem -> version.hasClassificationItem(xmlVersionItem.getCode()))
                .collect(Collectors.toList());

        if (!existing.isEmpty()) {
            throw new VersionImportException("Følgende koder finnes fra før:", existing);
        }
    }

    @Override
    protected ClassificationItem createClassificationItem(XmlVersionItem xmlItem, ClassificationVersion owner)
            throws ImportException {
        String validFromTxt = xmlItem.getValidFrom();
        String validToTxt = xmlItem.getValidTo();
        LocalDate validFrom;
        LocalDate validTo;
        try {
            validFrom = Strings.isNullOrEmpty(validFromTxt) ? null
                    : LocalDate.parse(validFromTxt, XmlVersionContainer.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ImportException("element med kode " + xmlItem.getCode() + " har en dato med ugyldig format: "
                    + validFromTxt + " (forventet format er " + XmlVersionContainer.DATE_FORMAT_STRING + ")");
        }
        try {
            validTo = Strings.isNullOrEmpty(validToTxt) ? null
                    : LocalDate.parse(validToTxt, XmlVersionContainer.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ImportException("element med kode " + xmlItem.getCode() + " har en dato med ugyldig format: "
                    + validToTxt + " (forventet format er " + XmlVersionContainer.DATE_FORMAT_STRING + ")");
        }

        return new ConcreteClassificationItem(xmlItem.getCode(),
                new Translatable(xmlItem.getNameNB(), xmlItem.getNameNN(), xmlItem.getNameEN()),
                new Translatable(xmlItem.getShortNameNB(), xmlItem.getShortNameNN(), xmlItem.getShortNameEN()),
                new Translatable(xmlItem.getNotesNB(), xmlItem.getNotesNN(), xmlItem.getNotesEN()),
                validFrom, validTo);

    }
}
