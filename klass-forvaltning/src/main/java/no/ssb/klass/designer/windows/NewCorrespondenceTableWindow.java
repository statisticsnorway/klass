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
@SuppressWarnings("serial")
public class NewCorrespondenceTableWindow extends Window {

    private NewCorrespondenceTableWindowLogic content;

    @Autowired
    public NewCorrespondenceTableWindow(ApplicationContext context) {
        content = context.getBean(NewCorrespondenceTableWindowLogic.class, this);
        setContent(content);
        center();
        setModal(true);
        setWidth(650, Unit.PIXELS);
    }

    public void init(Long versionId) {
        content.init(versionId);
    }
}
