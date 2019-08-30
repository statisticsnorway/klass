package no.ssb.klass.designer.windows;

import static com.google.common.base.Preconditions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.base.Strings;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

/**
 * @author Mads Lundemo, SSB.
 */
@SpringComponent
@PrototypeScope
public class UploadFileWindowLogic extends UploadFileWindowDesign {

    private Window parent;
    private FileUploadedListener listener;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    UploadFileWindowLogic(Window parent) {
        this.parent = parent;
        parent.addCloseListener(this::onCloseWindow);
        actionButtons.setConfirmText("Importer fil");
        actionButtons.disableConfirmButton(true, "Ingen fil valgt");
        actionButtons.addCancelClickListener(this::close);
        actionButtons.addConfirmClickListener(this::startUpload);

        uploadComponent.setButtonCaption(null); // hide default button
        uploadComponent.setReceiver(this::receiver);
        uploadComponent.addChangeListener(this::change);
        uploadComponent.addProgressListener(this::progression);
        uploadComponent.addFinishedListener(this::uploadFinished);
        uploadComponent.addFailedListener(this::uploadFailed);
        uploadComponent.addStartedListener(this::uploadStarted);

    }

    private void change(Upload.ChangeEvent changeEvent) {
        if (Strings.isNullOrEmpty(changeEvent.getFilename())) {
            actionButtons.disableConfirmButton(true, "Ingen fil valgt");
        } else {
            actionButtons.disableConfirmButton(false, "");
        }
    }

    private void onCloseWindow(Window.CloseEvent closeEvent) {
        freeStreamResource();
    }

    private void startUpload(Button.ClickEvent clickEvent) {
        if (!uploadComponent.isUploading()) {
            uploadComponent.submitUpload();
        }
    }

    private void close(Button.ClickEvent clickEvent) {
        uploadComponent.interruptUpload();
        freeStreamResource();
        parent.close();

    }

    private void freeStreamResource() {
        IOUtils.closeQuietly(outputStream);
    }

    private void uploadStarted(Upload.StartedEvent startedEvent) {
        progressbar.setValue(0.0f);
    }

    private void uploadFailed(Upload.FailedEvent failedEvent) {
        actionButtons.disableConfirmButton(true, null);

    }

    private void uploadFinished(Upload.FinishedEvent finishedEvent) {
        listener.fileUploaded(new ByteArrayInputStream(outputStream.toByteArray()));
        parent.close();

    }

    public void init(String title, FileUploadedListener listener) {
        this.listener = checkNotNull(listener);
        titleLabel.setValue(title);

    }

    private void progression(long readBytes, long contentLength) {
        progressbar.setValue(readBytes / (float) contentLength);
    }

    private OutputStream receiver(String filename, String mimeType) {
        outputStream.reset();
        return outputStream;
    }

    public interface FileUploadedListener {
        /**
         * NOTE: implementations of this interface is responsible for freeing/closing stream
         */
        void fileUploaded(InputStream stream);
    }
}
