package no.ssb.klass.api.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Exclude certain paths from Access Logs to prevent the logs being cluttered
 * with irrelevant requests. An example of clutter is health and monitoring
 * endpoint calls.
 */
@Component
public class AccessLogFilter implements Filter {
    private final List<String> excludePaths;

    public AccessLogFilter(
            @Value("${klass.env.log.access.exclude-paths}")
            List<String> excludePaths
    ) {
        this.excludePaths = excludePaths;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            String uri = ((HttpServletRequest) request).getRequestURI();

            if (excludePaths.stream().anyMatch(uri::startsWith)) {
                // Set the relevant attribute to a non-null value
                request.setAttribute("NO_LOG", "true");
            }
        }
        chain.doFilter(request, response);
    }
}
