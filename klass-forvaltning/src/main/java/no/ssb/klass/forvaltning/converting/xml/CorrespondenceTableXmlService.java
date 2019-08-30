package no.ssb.klass.forvaltning.converting.xml;

import static no.ssb.klass.forvaltning.converting.xml.dto.XmlCorrespondenceContainer.*;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.forvaltning.converting.exception.CorrespondenceTableImportException;
import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.forvaltning.converting.xml.abstracts.AbstractXmlService;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlCorrespondenceExportContainer;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;

/**
 * Service that takes care of xml import and export for Correspondence tables
 *
 * @author Mads Lundemo, SSB.
 */
@Service
@Profile(ConfigurationProfiles.FRONTEND_ONLY)
public class CorrespondenceTableXmlService extends AbstractXmlService<CorrespondenceTable> {


    public InputStream toXmlStream(CorrespondenceTable correspondenceTable) {
        XmlCorrespondenceExportContainer container = correspondenceTableToDto(correspondenceTable);
        ObjectWriter writer = getObjectWriter(XmlCorrespondenceExportContainer.class);
        return createInputStream(container, writer);
    }

    public void fromXmlStreamAndMerge(InputStream stream, CorrespondenceTable table) throws ImportException {

        List<XmlCorrespondenceItem> input = readInputStream(stream, XmlCorrespondenceItem.class);
        input = input.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList());

        List<XmlCorrespondenceItem> values = splittMultiValueCells(input);
        List<XmlCorrespondenceItem> failList = new LinkedList<>();
        List<CorrespondenceMap> mapList = new LinkedList<>();
        if (values.isEmpty()) {
            throw new ImportException("Filen inneholder ingen korrespondanser");
        }
        for (XmlCorrespondenceItem item : values) {
            try {
                Optional<CorrespondenceMap> map = createCorrespondenceMap(table, item);
                if (map.isPresent()) {
                    mapList.add(map.get());
                }
            } catch (IllegalArgumentException | ImportException e) {
                failList.add(item);
            }
        }

        if (failList.isEmpty()) {
            table.removeAllCorrespondenceMaps();
            mapList.forEach(table::addCorrespondenceMap);
        } else {
            throw new CorrespondenceTableImportException(failList);
        }

    }

    private List<XmlCorrespondenceItem> splittMultiValueCells(List<XmlCorrespondenceItem> input)
            throws ImportException {
        List<XmlCorrespondenceItem> split = new LinkedList<>();
        for (XmlCorrespondenceItem item : input) {
            if (item.isMultiCell()) {
                split.addAll(item.splitMultiCell());
            } else {
                split.add(item);
            }
        }
        return split;
    }

    private Optional<CorrespondenceMap> createCorrespondenceMap(CorrespondenceTable table,
            XmlCorrespondenceItem item) throws ImportException {
        if (item.getFromCode() == null && item.getToCode() == null) {
            return Optional.empty();
        } else {
            ClassificationItem from = null;
            ClassificationItem to = null;

            if (item.getFromCode() != null) {
                from = table.getSource().findItem(item.getFromCode());
            }
            if (item.getToCode() != null) {
                to = table.getTarget().findItem(item.getToCode());
            }

            return Optional.of(new CorrespondenceMap(from, to));
        }
    }

    protected XmlCorrespondenceExportContainer correspondenceTableToDto(CorrespondenceTable correspondenceTable) {
        Language sourceLanguage = correspondenceTable.getSource().getPrimaryLanguage();
        Language targetLanguage = correspondenceTable.getTarget().getPrimaryLanguage();
        List<XmlCorrespondenceItem> list = correspondenceTable.getCorrespondenceMaps()
                .stream()
                .map(map -> new XmlCorrespondenceItem(map, sourceLanguage, targetLanguage))
                .sorted((o1, o2) -> o1.getFromCode().compareTo(o2.getFromCode()))
                .collect(Collectors.toCollection(LinkedList::new));

        XmlCorrespondenceExportContainer container = new XmlCorrespondenceExportContainer(list);
        container.setName(correspondenceTable.getNameInPrimaryLanguage());
        container.setSchemaBaseUrl(SchemaBaseUrl);
        return container;
    }
}
