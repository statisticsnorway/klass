package no.ssb.klass.api.controllers.handlers;

import jakarta.servlet.http.HttpServletRequest;

import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.core.util.KlassResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class Handlers {
    private static final Logger log = LoggerFactory.getLogger(Handlers.class);

    private static final String EXCEPTION_HANDLER_LOG_MESSAGE_TEMPLATE = "{}. For request: {}";

    @ExceptionHandler(
            exception = KlassResourceNotFoundException.class,
            produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                MediaType.TEXT_XML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.APPLICATION_PROBLEM_XML_VALUE
            })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail resourceNotFoundProblemDetailExceptionHandler(
            KlassResourceNotFoundException exception, HttpServletRequest request) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(
            exception = KlassResourceNotFoundException.class,
            produces = {MediaType.TEXT_PLAIN_VALUE, RestConstants.CONTENT_TYPE_CSV})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String resourceNotFoundTextExceptionHandler(
            KlassResourceNotFoundException exception, HttpServletRequest request) {
        return exception.getMessage();
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
            },
            produces = {MediaType.TEXT_PLAIN_VALUE, RestConstants.CONTENT_TYPE_CSV})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String badRequestTextExceptionHandler(Exception exception) {
        return exception.getMessage();
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
        log.error(
                EXCEPTION_HANDLER_LOG_MESSAGE_TEMPLATE,
                exception.getMessage(),
                request.getRequestURI(),
                exception);
        return exception.getMessage();
    }
}
