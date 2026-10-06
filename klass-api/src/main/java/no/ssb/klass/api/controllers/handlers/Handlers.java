package no.ssb.klass.api.controllers.handlers;

import jakarta.servlet.http.HttpServletRequest;

import no.ssb.klass.core.util.KlassResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.DisconnectedClientHelper;

@RestControllerAdvice
public class Handlers {
    private static final Logger log = LoggerFactory.getLogger(Handlers.class);

    private static final String EXCEPTION_HANDLER_LOG_MESSAGE_TEMPLATE = "{}. For request: {}";

    private static final String CLIENT_DISCONNECTED_LOG_MESSAGE_TEMPLATE =
            "Client disconnected before the response was fully written. For request: {}";

    @ExceptionHandler(
            exception = {KlassResourceNotFoundException.class, NoHandlerFoundException.class},
            produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                MediaType.TEXT_XML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.APPLICATION_PROBLEM_XML_VALUE
            })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail resourceNotFoundProblemDetailExceptionHandler(Exception exception) {
        if (exception.getClass() == KlassResourceNotFoundException.class) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        }
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(
            exception = {KlassResourceNotFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String resourceNotFoundTextExceptionHandler(Exception exception) {
        if (exception.getClass() == KlassResourceNotFoundException.class) {
            return exception.getMessage();
        }
        return "";
    }

    @ExceptionHandler(
            exception = {
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
    public ProblemDetail badRequestProblemDetailExceptionHandler(Exception exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(
            exception = {
                RestClientException.class,
                MethodArgumentTypeMismatchException.class,
                IllegalArgumentException.class,
                MissingServletRequestParameterException.class
            })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String badRequestTextExceptionHandler(Exception exception) {
        return exception.getMessage();
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
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String serverErrorTextExceptionHandler(Exception exception, HttpServletRequest request) {
        if (isClientDisconnected(exception, request)) {
            return null;
        }
        log.error(
                EXCEPTION_HANDLER_LOG_MESSAGE_TEMPLATE,
                exception.getMessage(),
                request.getRequestURI(),
                exception);
        return exception.getMessage();
    }
}
