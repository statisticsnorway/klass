package no.ssb.klass.api.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

/**
 * Buffers the response body so that every response declares a {@code Content-Length}.
 *
 * <p>Large payloads overflow Tomcat's 8 KB output buffer, so the response is committed as {@code
 * 200} with {@code Transfer-Encoding: chunked} long before the body is complete. Without a declared
 * length, a body that is cut short -- by a proxy read timeout, or by us failing mid-write -- is
 * indistinguishable from a complete one, and the client silently accepts a partial result.
 * Declaring the length up front turns a truncated response into an error the client can detect.
 *
 * <p>As a side effect this also populates the {@code content_length} field in the access log, which
 * reports the declared header and is therefore {@code -1} for chunked responses.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContentLengthFilter extends OncePerRequestFilter {
    /** Run on async dispatches too, so the body is copied on the last dispatched thread. */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletResponse responseToUse = response;
        if (!isAsyncDispatch(request) && !(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new ContentCachingResponseWrapper(response);
        }

        filterChain.doFilter(request, responseToUse);

        // On an exception the buffered body is intentionally discarded, leaving the container free
        // to write an error response instead of a half-written one.
        if (!isAsyncStarted(request)) {
            ContentCachingResponseWrapper wrapper =
                    WebUtils.getNativeResponse(responseToUse, ContentCachingResponseWrapper.class);
            if (wrapper != null) {
                wrapper.copyBodyToResponse();
            }
        }
    }
}
