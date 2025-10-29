package no.ssb.klass.api.filters;

import no.ssb.klass.api.util.RestConstants;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class AccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();

        // Allow Klass web and other frontends to serve data from the API
        if (path.startsWith(RestConstants.CONTEXT_PATH)) {
            res.addHeader("Access-Control-Allow-Origin", "*");
            res.addHeader("Vary", "Accept");
            res.addHeader("Vary", "X-Forwarded-Proto");
        }
        chain.doFilter(request, response);
    }
}
