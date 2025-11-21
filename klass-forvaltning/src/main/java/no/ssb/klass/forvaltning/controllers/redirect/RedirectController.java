package no.ssb.klass.forvaltning.controllers.redirect;

import no.ssb.klass.designer.ui.KlassUI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {

    private final String serverContextPath;

    public RedirectController(
            @Value("${server.context-path}")
            String serverContextPath
    ) {
        this.serverContextPath = serverContextPath;
    }

    @GetMapping("/login")
    public RedirectView redirectLogin(RedirectAttributes attributes) {
        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        attributes.addAttribute("attribute", "redirectWithRedirectView");
        return new RedirectView(this.serverContextPath + "/" + KlassUI.PATH, false);
    }

}
