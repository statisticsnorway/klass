package no.ssb.klass.api.controllers.handlers;

import jakarta.servlet.http.HttpServletRequest;

import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.core.util.ClientInputException;
import no.ssb.klass.core.util.KlassResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.DisconnectedClientHelper;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@RestControllerAdvice
public class Handlers {
    private static final Logger log = LoggerFactory.getLogger(Handlers.class);

    private static final Pattern SAFE_PATH = Pattern.compile("[A-Za-z0-9/._~-]*");
    private static final MediaType CSV_MEDIA_TYPE =
            MediaType.parseMediaType(RestConstants.CONTENT_TYPE_CSV);

    private final ContentNegotiationManager contentNegotiationManager;

    private static final String EXCEPTION_HANDLER_LOG_MESSAGE_TEMPLATE = "{}. For request: {}";

    private static final String CLIENT_DISCONNECTED_LOG_MESSAGE_TEMPLATE =
            "Client disconnected before the response was fully written. For request: {}";

    public Handlers(ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @ExceptionHandler(
            exception = {
                KlassResourceNotFoundException.class,
                NoHandlerFoundException.class,
                NoResourceFoundException.class
            },
            produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                MediaType.TEXT_XML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.APPLICATION_PROBLEM_XML_VALUE
            })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail resourceNotFoundProblemDetailExceptionHandler(
            Exception exception, HttpServletRequest request) {
        return problemDetail(HttpStatus.NOT_FOUND, resourceNotFoundMessage(exception), request);
    }

    @ExceptionHandler(
            exception = {
                KlassResourceNotFoundException.class,
                NoHandlerFoundException.class,
                NoResourceFoundException.class
            })
    public ResponseEntity<String> resourceNotFoundTextExceptionHandler(
            Exception exception, HttpServletRequest request)
            throws HttpMediaTypeNotAcceptableException {
        return textErrorResponse(HttpStatus.NOT_FOUND, resourceNotFoundMessage(exception), request);
    }

    private String resourceNotFoundMessage(Exception exception) {
        if (exception instanceof KlassResourceNotFoundException) {
            return exception.getMessage();
        }
        return HttpStatus.NOT_FOUND.getReasonPhrase();
    }

    @ExceptionHandler(
            exception = {
                ClientException.class,
                RestClientException.class,
                MethodArgumentTypeMismatchException.class,
                IllegalArgumentException.class,
                MissingServletRequestParameterException.class,
                java.lang.NumberFormatException.class
            },
            produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                MediaType.TEXT_XML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.APPLICATION_PROBLEM_XML_VALUE
            })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail badRequestProblemDetailExceptionHandler(
            Exception exception, HttpServletRequest request) {
        return problemDetail(HttpStatus.BAD_REQUEST, badRequestMessage(exception), request);
    }

    @ExceptionHandler(
            exception = {
                ClientException.class,
                RestClientException.class,
                MethodArgumentTypeMismatchException.class,
                IllegalArgumentException.class,
                MissingServletRequestParameterException.class
            })
    public ResponseEntity<String> badRequestTextExceptionHandler(
            Exception exception, HttpServletRequest request)
            throws HttpMediaTypeNotAcceptableException {
        return textErrorResponse(HttpStatus.BAD_REQUEST, badRequestMessage(exception), request);
    }

    private String badRequestMessage(Exception exception) {
        if (exception instanceof ClientException
                || exception instanceof ClientInputException
                || exception instanceof MethodArgumentTypeMismatchException
                || exception instanceof MissingServletRequestParameterException) {
            return exception.getMessage();
        }
        return HttpStatus.BAD_REQUEST.getReasonPhrase();
    }

    /**
     * Spring fills in {@code instance} from the raw request path when it is null, which would
     * reflect attacker-controlled input. Only plain path characters are echoed back.
     */
    private ProblemDetail problemDetail(
            HttpStatus status, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        String path = request.getRequestURI();
        problem.setInstance(
                URI.create(path != null && SAFE_PATH.matcher(path).matches() ? path : "/"));
        return problem;
    }

    private ResponseEntity<String> textErrorResponse(
            HttpStatus status, String message, HttpServletRequest request)
            throws HttpMediaTypeNotAcceptableException {
        MediaType contentType =
                contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request)).stream()
                        .filter(type -> type.getQualityValue() > 0)
                        .findFirst()
                        .filter(CSV_MEDIA_TYPE::equalsTypeAndSubtype)
                        .map(type -> CSV_MEDIA_TYPE)
                        .orElse(MediaType.TEXT_PLAIN);
        return ResponseEntity.status(status)
                .contentType(new MediaType(contentType, StandardCharsets.UTF_8))
                .header("X-Content-Type-Options", "nosniff")
                .body(message);
    }

    /**
     * The client went away before we finished writing the response. The response is already
     * committed, so there is nothing to report back and no status left to set. Log at DEBUG so
     * these do not drown out genuine server faults.
     */
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void clientDisconnectedExceptionHandler(
            AsyncRequestNotUsableException exception, HttpServletRequest request) {
        log.debug(CLIENT_DISCONNECTED_LOG_MESSAGE_TEMPLATE, request.getRequestURI(), exception);
    }

    /**
     * Catches disconnect variants that are not matched by type, so they are not reported as server
     * faults by {@link #serverErrorProblemDetailExceptionHandler} and {@link
     * #serverErrorTextExceptionHandler}.
     */
    private boolean isClientDisconnected(Exception exception, HttpServletRequest request) {
        if (!DisconnectedClientHelper.isClientDisconnectedException(exception)) {
            return false;
        }
        log.debug(CLIENT_DISCONNECTED_LOG_MESSAGE_TEMPLATE, request.getRequestURI(), exception);
        return true;
    }

    @ExceptionHandler(
            produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                MediaType.TEXT_XML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.APPLICATION_PROBLEM_XML_VALUE
            })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail serverErrorProblemDetailExceptionHandler(
            Exception exception, HttpServletRequest request) {
        if (isClientDisconnected(exception, request)) {
            return null;
        }
        log.error(
                EXCEPTION_HANDLER_LOG_MESSAGE_TEMPLATE,
                exception.getMessage(),
                request.getRequestURI(),
                exception);
        return problemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                request);
    }

    @ExceptionHandler
    public ResponseEntity<String> serverErrorTextExceptionHandler(
            Exception exception, HttpServletRequest request)
            throws HttpMediaTypeNotAcceptableException {
        if (isClientDisconnected(exception, request)) {
            return null;
        }
        log.error(
                EXCEPTION_HANDLER_LOG_MESSAGE_TEMPLATE,
                exception.getMessage(),
                request.getRequestURI(),
                exception);
        return textErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                request);
    }
}
