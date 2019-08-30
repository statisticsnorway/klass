package no.ssb.klass.designer;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;

import no.ssb.klass.designer.ui.KlassUI;

public class KlassSystemMessageProvider implements SystemMessagesProvider {

    @Override
    public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
        CustomizedSystemMessages message = new CustomizedSystemMessages();
        message.setCommunicationErrorCaption("Kommunikasjonssvikt med server");
        message.setCommunicationErrorMessage("Serveren responderer ikke. Rapporter problemet til drift!");
        message.setSessionExpiredCaption("Sesjonen er utløpt!");
        message.setSessionExpiredMessage("Tast ESC her, og start på ny :-(");        
        message.setCommunicationErrorNotificationEnabled(false);
        message.setCommunicationErrorURL(KlassUI.PATH);
        message.setCookiesDisabledURL("about:blank");
        message.setInternalErrorCaption("En alvorlig feil har oppstått i KLASS!");
        message.setInternalErrorMessage("Prøv å start KLASS på ny, og kontakt support hvis feilen vedvarer.");
        return message;
    }

}
