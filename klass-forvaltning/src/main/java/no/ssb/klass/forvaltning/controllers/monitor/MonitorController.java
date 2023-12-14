package no.ssb.klass.forvaltning.controllers.monitor;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.search.SolrSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.result.FacetAndHighlightPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import no.ssb.klass.core.repository.UserRepository;

/**
 * @author Mads Lundemo, SSB.
 */
@Controller
@RequestMapping(MonitorController.PATH)
public class MonitorController {
    public static final String PATH = "/monitor/";

    private static final String DATABASE_TILKOBLING = "Database tilkobling";
    private static final String KLASS_FORVALTNING = "Klass Forvaltning";
    private static final String SOLR_SEARCH = "solr søk";

    @Value("${info.build.version:Unknown}")
    private String version;

    @Autowired
    private UserRepository repository;

    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String render(HttpServletRequest request, ModelMap model) {

        List<MonitorStatus> statusList = new LinkedList<>();
        statusList.add(testDatabaseConnection());
        statusList.add(testForvaltning(request));
        statusList.add(testSearch());

        model.addAttribute("version", version);
        model.addAttribute("statusList", statusList);

        return "monitor";
    }

    private MonitorStatus testDatabaseConnection() {
        try {
            long count = repository.count();
            return new MonitorStatus(DATABASE_TILKOBLING, true,
                    "Alt OK! (fant " + count + " brukere med test spørring)");
        } catch (Exception e) {
            return new MonitorStatus(DATABASE_TILKOBLING, false, "FEIL: " + e.getMessage());
        }
    }


    private MonitorStatus testForvaltning(HttpServletRequest request) {
        try {
            String currentUrl = getCurrentUrl(request);

            String testUrl = currentUrl + "/login";
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return new MonitorStatus(KLASS_FORVALTNING, true,
                        "Alt OK! (" + testUrl + " Returnerte status 200)");
            } else {
                return new MonitorStatus(KLASS_FORVALTNING, false,
                        " FEIL: Uventet svar (" + testUrl + " Returnerte status " + responseCode + ")");
            }
        } catch (Exception e) {
            return new MonitorStatus(KLASS_FORVALTNING, false, " Feil ved testing :" + e.getMessage());
        }
    }

    private MonitorStatus testSearch() {
        try {
            FacetAndHighlightPage<SolrSearchResult> solrSearchResults =
                    searchService.publicSearch("*", PageRequest.of(0, 10), null, true);
            int results = solrSearchResults.getSize();
            if (results > 0) {
                return new MonitorStatus(SOLR_SEARCH, true, "Søk fungerer");
            } else {
                return new MonitorStatus(SOLR_SEARCH, false, "Solr returnerte ingen treff");
            }
        } catch (Exception e) {
            return new MonitorStatus(SOLR_SEARCH, false, "Feil ved testing :" + e.getMessage());
        }
    }

    private static String getCurrentUrl(HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        String header = request.getHeader("X-Forwarded-Proto");

        URL url = new URL(request.getRequestURL().toString());
        String host = url.getHost();
        String userInfo = url.getUserInfo();
        String scheme = header != null ? header :url.getProtocol();
        int port = url.getPort();
        String path = request.getContextPath();

        URI uri = new URI(scheme, userInfo, host, port, path, null, null);
        return uri.toString();

    }

}
