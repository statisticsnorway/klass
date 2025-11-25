package no.ssb.klass.forvaltning.controllers.redirect;

import no.ssb.klass.designer.ui.KlassUI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

@Controller
public class RedirectController {

    private final String serverContextPath;

    public RedirectController(@Value("${server.context-path}") String serverContextPath) {
        this.serverContextPath = serverContextPath;
    }

    @GetMapping("/login")
    public void redirectLogin(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect(this.serverContextPath + "/" + KlassUI.PATH);
    }
}
