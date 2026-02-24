package no.ssb.klass.designer.windows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;

/**
 * @author Mads Lundemo, SSB.
 */
@SpringComponent
@PrototypeScope
public class UploadFileWindow extends Window {

    private UploadFileWindowLogic content;

    @Autowired
    public UploadFileWindow(ApplicationContext context) {
        content = context.getBean(UploadFileWindowLogic.class, this);
        setContent(content);
        center();
        setModal(true);
        setResizable(false);
    }

    public void init(String title, UploadFileWindowLogic.FileUploadedListener listener) {
        content.init(title, listener);
    }

}
