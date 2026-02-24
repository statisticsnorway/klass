package no.ssb.klass.designer.vaadin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * @author Mads Lundemo, SSB.
 * 
 *         This class is a replacement for the default vaadin componentFactory so we get spring autowiring.
 * 
 */
public class VaadinSpringComponentFactory extends Design.DefaultComponentFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Component createComponent(String fullyQualifiedClassName, DesignContext context) {
        Component component = super.createComponent(fullyQualifiedClassName, context);
        // Ignoring autowire for default vaadin components (might slightly improve performance)
        if (!fullyQualifiedClassName.contains("com.vaadin.ui")) {
            applicationContext.getAutowireCapableBeanFactory().autowireBean(component);
        }
        return component;
    }
}
