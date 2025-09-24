package no.ssb.klass.api.filters;

import no.ssb.klass.api.controllers.PingController;
import no.ssb.klass.api.util.RestConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();

        if (path.equals("/manage") || path.startsWith("/manage/")) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (path.startsWith(RestConstants.API_VERSION_V1) ||
                path.equals(PingController.PATH)) {

            if (path.startsWith(RestConstants.PREFIX_AND_API_VERSION_V1)) {
                res.addHeader("Access-Control-Allow-Origin", "*");
                res.addHeader("Vary", "Accept");
                res.addHeader("Vary", "X-Forwarded-Proto");
            }

            chain.doFilter(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

}
