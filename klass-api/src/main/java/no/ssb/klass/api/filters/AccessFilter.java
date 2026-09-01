package no.ssb.klass.api.filters;

import io.micrometer.core.instrument.Metrics;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;

@WebFilter("*")
public class AccessFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AccessFilter.class);

    /**
     * Counts the number of writing failures.
     *
     * @param req: HttpServletRequest
     * @param res: HttpServletResponse
     * @param e: Exception
     */
    private void countWriteFailure(HttpServletRequest req, HttpServletResponse res, Exception e) {
        String endpoint = (String) req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (endpoint == null) {
            endpoint = "unmapped";
        }

        Metrics.counter(
                        "klass_response_write_failures_total",
                        "method",
                        req.getMethod(),
                        "endpoint",
                        endpoint,
                        "exception",
                        e.getClass().getSimpleName(),
                        "committed",
                        Boolean.toString(res.isCommitted()))
                .increment();
    }

    /**
     * Format a readable request path in logstash format.
     *
     * @param req: HttpServletRequest
     * @return String: request path
     */
    private String requestTarget(HttpServletRequest req) {
        String queryString = req.getQueryString();
        return queryString == null ? req.getRequestURI() : req.getRequestURI() + "?" + queryString;
    }

    @SuppressWarnings(
            "java:S2139") // Intentional: in write-phase failures we must record metric context here
    // and rethrow so container error handling remains unchanged
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Headers instructing the Varnish cache when to invalidate
        res.addHeader("Vary", "Accept");
        res.addHeader("Vary", "X-Forwarded-Proto");
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException e) {
            log.error(
                    "Response processing failed for {} {} (committed={}, status={}, contentType={})",
                    req.getMethod(),
                    requestTarget(req),
                    res.isCommitted(),
                    res.getStatus(),
                    res.getContentType(),
                    e);
            countWriteFailure(req, res, e);
            throw e;
        }
    }
}
