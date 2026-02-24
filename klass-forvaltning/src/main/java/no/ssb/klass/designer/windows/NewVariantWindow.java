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
public class NewVariantWindow extends Window {

    private NewVariantWindowLogic content;

    @Autowired
    public NewVariantWindow(ApplicationContext context) {
        content = context.getBean(NewVariantWindowLogic.class, this);
        setContent(content);
        center();
        setModal(true);
        setResizable(false);
    }

    public void init(Long versionId) {
        content.init(versionId);
    }
}
