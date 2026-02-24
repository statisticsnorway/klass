package no.ssb.klass.designer.editing.components;

import java.io.InputStream;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.forvaltning.converting.xml.abstracts.AbstractXmlService;
import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Publishable;
import no.ssb.klass.designer.util.ConfirmationDialog;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.windows.UploadFileWindow;

/**
 * @author Mads Lundemo, SSB.
 */
public class ImportExportComponent<T extends ClassificationEntityOperations> {

    private static final Logger log = LoggerFactory.getLogger(ParameterUtil.class);

    @FunctionalInterface
    public interface CheckedConsumer<T> {
        void accept(T t) throws ImportException;
    }

    private final StreamResource streamResource = new StreamResource(this::generateExportData, "");
    private final FileDownloader fileDownloader = new FileDownloader(streamResource);

    private ApplicationContext applicationContext;
    private AbstractXmlService<T> xmlService;

    protected final Button importButton;
    protected final Button exportButton;

    private Consumer<Boolean> onCompleteCallback;
    private CheckedConsumer<T> clearEntityCallback;

    protected T entity;
    protected String datatypeName;

    public ImportExportComponent(ApplicationContext applicationContext, AbstractXmlService<T> xmlService,
            Button importButton, Button exportButton) {
        this.applicationContext = applicationContext;
        this.importButton = importButton;
        this.exportButton = exportButton;
        this.xmlService = xmlService;

        importButton.addClickListener(this::importClick);
        fileDownloader.extend(exportButton);
    }

    public void init(T entity, String datatypeName) {
        this.entity = entity;
        this.datatypeName = datatypeName;
        streamResource.setFilename(xmlService.createFileName(entity));
        if (entity instanceof Publishable && entity.isPublishedInAnyLanguage()) {
            disableImportButton(true, "Import er deaktivert for gitt " + datatypeName
                    + " er publisert på ett eller flere språk");
        }
    }

    private void disableImportButton(boolean disable, String reason) {
        importButton.setEnabled(!disable);
        importButton.setDescription(reason);
    }

    private InputStream generateExportData() {
        return xmlService.toXmlStream(entity);
    }

    private void showImportDialog() {
        UploadFileWindow uploadFileWindow = applicationContext.getBean(UploadFileWindow.class);
        uploadFileWindow.init("Importer " + datatypeName, this::uploadedFileHandler);
        UI.getCurrent().addWindow(uploadFileWindow);
    }

    private void uploadedFileHandler(InputStream stream) {
        boolean success = true;
        try {
            if (clearEntityCallback != null) {
                clearEntityCallback.accept(entity);
            }
            xmlService.fromXmlStreamAndMerge(stream, entity);
        } catch (ImportException e) {
            success = false;
            Notification.show("Importering mislykket", e.getMessage(), Notification.Type.WARNING_MESSAGE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            IOUtils.closeQuietly(stream);
            if (onCompleteCallback != null) {
                onCompleteCallback.accept(success);
            }
        }

    }

    private void confirmResponse(boolean confirm) {
        if (confirm) {
            showImportDialog();
        }
    }

    public void setOnCompleteCallback(Consumer<Boolean> onCompleteCallback) {
        this.onCompleteCallback = onCompleteCallback;
    }

    public void setClearEntityCallback(CheckedConsumer<T> clearEntityCallback) {
        this.clearEntityCallback = clearEntityCallback;
    }

    private void importClick(Button.ClickEvent clickEvent) {
        if (isEmpty(entity)) {
            showImportDialog();
        } else {
            String message = datatypeName + "en inneholder elementer.<br>"
                    + "Eksisterende elementer vil bli erstattet ved import.<br>"
                    + "Ønsker du å fortsette?";
            ConfirmationDialog confirmationDialog =
                    new ConfirmationDialog("Ja, erstatt eksisterende", message, this::confirmResponse);
            confirmationDialog.setWidth(500, Sizeable.Unit.PIXELS);
            UI.getCurrent().addWindow(confirmationDialog);

        }
    }

    private boolean isEmpty(T entity) {
        if (entity instanceof CorrespondenceTable) {
            return ((CorrespondenceTable) entity).getCorrespondenceMaps().isEmpty();
        } else if (entity instanceof ClassificationVersion) {
            return ((ClassificationVersion) entity).getAllClassificationItems().isEmpty();
        } else if (entity instanceof ClassificationVariant) {
            return ((ClassificationVariant) entity).getAllClassificationItems().isEmpty();
        }
        return true;
    }

}
