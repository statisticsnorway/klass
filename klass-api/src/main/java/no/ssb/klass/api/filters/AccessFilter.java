package no.ssb.klass.api.filters;

import no.ssb.klass.api.util.RestConstants;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

            if (path.startsWith(RestConstants.PREFIX_AND_API_VERSION_V1)) {
                res.addHeader("Access-Control-Allow-Origin", "*");
                res.addHeader("Vary", "Accept");
                res.addHeader("Vary", "X-Forwarded-Proto");
            }

        }

        chain.doFilter(request, response);
    }

}
