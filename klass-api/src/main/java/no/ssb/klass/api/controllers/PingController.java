package no.ssb.klass.api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mads Lundemo, SSB.
 */
@Controller
@RequestMapping(PingController.PATH)
public class PingController {
    public static final String PATH = "/ping/";

    @Value("${info.build.version:Unknown}")
    private String version;

    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/xml")
    public String ping() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<ping>\n"
                + "   <version>" + version + "</version>\n"
                + "</ping>";
    }

}
