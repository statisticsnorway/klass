package no.ssb.klass.api.filters;


import java.io.IOException;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("*")
public class AccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        // Headers instructing the Varnish cache when to invalidate
        res.addHeader("Vary", "Accept");
        res.addHeader("Vary", "X-Forwarded-Proto");
        chain.doFilter(request, response);
    }
}
