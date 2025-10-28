package no.ssb.klass.api.controllers;

import io.swagger.v3.oas.annotations.Hidden;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mads Lundemo, SSB.
 */
@Hidden
@RestController
public class PingController {
    public static final String PATH = "/ping";

    @Value("${info.build.version:Unknown}")
    private String version;

    @ResponseBody
    @RequestMapping(value = {
            "/api/klass" + PingController.PATH,
            "/api/klass" + PingController.PATH + "/",
            PingController.PATH,
            PingController.PATH + "/"
    }, method = RequestMethod.GET, produces = "application/xml")
    public String ping() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<ping>\n"
                + "   <version>" + version + "</version>\n"
                + "</ping>";
    }

}
