package no.ssb.klass.api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static no.ssb.klass.api.controllers.MonitorController.*;

/**
 * @author Mads Lundemo, SSB.
 */
@Controller
@RequestMapping(value = {
        PingController.PATH,
        PingController.PATH + "/",
        REST_URL_PREFIX + PingController.PATH,
        REST_URL_PREFIX + PingController.PATH + "/",
})
public class PingController {
    public static final String PATH = "/ping";

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
