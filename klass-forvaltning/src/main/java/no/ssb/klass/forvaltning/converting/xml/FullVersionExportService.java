package no.ssb.klass.forvaltning.converting.xml;

import java.io.InputStream;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.forvaltning.converting.xml.abstracts.AbstractXmlService;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionWithChildrenContainer;
import no.ssb.klass.forvaltning.converting.xml.fullversionexport.XmlVersionWithChildrenBuilder;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.service.ClassificationService;

/**
 * @author Mads Lundemo, SSB.
 */
@Service
@Profile(ConfigurationProfiles.FRONTEND_ONLY)
public class FullVersionExportService extends AbstractXmlService<ClassificationVersion> {

    @Autowired
    private ClassificationService classificationService;

    @Override
    public InputStream toXmlStream(ClassificationVersion version) {
        XmlVersionWithChildrenContainer container = versionToDto(version);
        ObjectWriter writer = getObjectWriter(XmlVersionWithChildrenContainer.class);
        return createInputStream(container, writer);

    }

    @Override
    public void fromXmlStreamAndMerge(InputStream stream, ClassificationVersion domainObject) throws ImportException {
        throw new ImportException("Import not supported");
    }

    private XmlVersionWithChildrenContainer versionToDto(ClassificationVersion version) {
        ClassificationVersion classificationVersion = classificationService.getClassificationVersion(version
                .getId());
        XmlVersionWithChildrenBuilder builder =
                new XmlVersionWithChildrenBuilder(classificationService, classificationVersion);
        return builder.build();
    }

}
