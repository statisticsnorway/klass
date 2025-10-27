package no.ssb.klass.api.filters;

import no.ssb.klass.api.util.RestConstants;

import javax.servlet.*;
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

        if (path.startsWith(RestConstants.API_VERSION_V1)) {

            if (path.startsWith(RestConstants.CONTEXT_AND_VERSION_V1)) {
                res.addHeader("Access-Control-Allow-Origin", "*");
                res.addHeader("Vary", "Accept");
                res.addHeader("Vary", "X-Forwarded-Proto");
            }

        }

        chain.doFilter(request, response);
    }

}
