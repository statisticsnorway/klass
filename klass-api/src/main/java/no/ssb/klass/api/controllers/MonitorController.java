package no.ssb.klass.api.controllers;

import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.core.service.search.SolrSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.result.FacetAndHighlightPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mads Lundemo, SSB.
 */
@Controller
@RequestMapping(MonitorController.PATH)
public class MonitorController {
    private static final String REST_URL_PREFIX = "/api/klass/v1";
    public static final String PATH = "/monitor/";
    
    private static final String DATABASE_TILKOBLING = "Database tilkobling";
    private static final String REST_API = "Rest API";
    private static final String SOLR_SEARCH = "solr søk";
    

    @Value("${info.build.version:Unknown}")
    private String version;
    
    @Autowired
    private UserService userService;

    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String render(HttpServletRequest request, ModelMap model) {

        List<MonitorStatus> statusList = new LinkedList<>();
        statusList.add(testDatabaseConnection());
        statusList.add(testRestAPI(request));
        statusList.add(testSearch());

        model.addAttribute("version", version);
        model.addAttribute("statusList", statusList);

        return "monitor";
    }

    private MonitorStatus testDatabaseConnection() {
        try {
            long count = userService.getUsersCount();
            return new MonitorStatus(DATABASE_TILKOBLING, true,
                    "Alt OK! (fant " + count + " brukere med test spørring)");
        } catch (Exception e) {
            return new MonitorStatus(DATABASE_TILKOBLING, false, "FEIL: " + e.getMessage());
        }
    }

    private MonitorStatus testRestAPI(HttpServletRequest request) {
        try {
            String currentUrl = getCurrentUrl(request);

            String testUrl = currentUrl + REST_URL_PREFIX + "/classifications";
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return new MonitorStatus(REST_API, true,
                        "Alt OK! (" + testUrl + " Returnerte status 200)");
            } else {
                return new MonitorStatus(REST_API, false,
                        " FEIL: Uventet svar (" + testUrl + "Returnerte status " + responseCode + ")");
            }
        } catch (Exception e) {
            return new MonitorStatus(REST_API, false, "Feil ved testing :" + e.getMessage());
        }
    }

    private MonitorStatus testSearch() {
        try {
            FacetAndHighlightPage<SolrSearchResult> solrSearchResults = 
                    searchService.publicSearch("*",PageRequest.of(0,10),null,true);
            int results = solrSearchResults.getSize();
            if (results>0) {
                return new MonitorStatus(SOLR_SEARCH, true, "Søk fungerer");
            }else {
                return new MonitorStatus(SOLR_SEARCH, false, "Solr returnerte ingen treff");
            }
        } catch (Exception e) {
            return new MonitorStatus(SOLR_SEARCH, false, "Feil ved testing :" + e.getMessage());
        }
    }

    private static String getCurrentUrl(HttpServletRequest request) throws MalformedURLException, URISyntaxException {
            URL url = new URL(request.getRequestURL().toString());
            String host = url.getHost();
            String userInfo = url.getUserInfo();
            String scheme = url.getProtocol();
            int port = url.getPort();
            String path = (String) request.getAttribute("javax.servlet.forward.request_uri");
            String query = (String) request.getAttribute("javax.servlet.forward.query_string");

            URI uri = new URI(scheme, userInfo, host, port, path, query, null);
            return uri.toString();

    }

}
