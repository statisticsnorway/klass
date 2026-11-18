package no.ssb.klass.api.controllers.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.core.util.ClientInputException;
import no.ssb.klass.core.util.KlassResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.stream.Stream;

class HandlersTest {

    private static final String PRIVATE_DETAIL =
            "private-error-detail<script>alert(\"xss\")</script>";

    private static final String PUBLIC_DETAIL =
            "Unknown value: <script>alert(\"xss\")</script> & \"quoted\"";

    private final Handlers handlers = new Handlers(new ContentNegotiationManager());
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private static Stream<String> acceptedContentTypes() {
        return Stream.of(
                MediaType.TEXT_PLAIN_VALUE,
                MediaType.TEXT_HTML_VALUE,
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "text/csv",
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.TEXT_XML_VALUE,
                MediaType.APPLICATION_PROBLEM_XML_VALUE);
    }

    private static Stream<Arguments> errorResponses() {
        return acceptedContentTypes()
                .flatMap(
                        accept ->
                                Stream.of(
                                        Arguments.of(
                                                new NoHandlerFoundException(
                                                        "GET", PRIVATE_DETAIL, HttpHeaders.EMPTY),
                                                HttpStatus.NOT_FOUND,
                                                accept),
                                        Arguments.of(
                                                new NoResourceFoundException(
                                                        HttpMethod.GET, PRIVATE_DETAIL),
                                                HttpStatus.NOT_FOUND,
                                                accept),
                                        Arguments.of(
                                                new IllegalArgumentException(PRIVATE_DETAIL),
                                                HttpStatus.BAD_REQUEST,
                                                accept),
                                        Arguments.of(
                                                new RestClientException(PRIVATE_DETAIL),
                                                HttpStatus.BAD_REQUEST,
                                                accept),
                                        Arguments.of(
                                                new IllegalStateException(PRIVATE_DETAIL),
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                accept)));
    }

    private static Stream<Arguments> clientErrors() {
        return acceptedContentTypes()
                .flatMap(
                        accept ->
                                Stream.of(
                                        Arguments.of(
                                                new KlassResourceNotFoundException(PUBLIC_DETAIL),
                                                HttpStatus.NOT_FOUND,
                                                accept),
                                        Arguments.of(
                                                new ClientInputException(PUBLIC_DETAIL),
                                                HttpStatus.BAD_REQUEST,
                                                accept),
                                        Arguments.of(
                                                new ClientException(PUBLIC_DETAIL),
                                                HttpStatus.BAD_REQUEST,
                                                accept)));
    }

    @ParameterizedTest
    @MethodSource("clientErrors")
    void clientErrorDetailsArePreservedWithoutRenderingHtml(
            Exception exception, HttpStatus expectedStatus, String accept) throws Exception {
        ResultActions result =
                mvcFor(exception)
                        .perform(get("/error").accept(accept))
                        .andExpect(status().is(expectedStatus.value()));

        if (accept.endsWith("json")) {
            result.andExpect(content().contentTypeCompatibleWith(accept))
                    .andExpect(jsonPath("$.detail").value(PUBLIC_DETAIL));
        } else if (accept.endsWith("xml")) {
            result.andExpect(content().contentTypeCompatibleWith(accept))
                    .andExpect(
                            xpath("/*[local-name()='problem']/*[local-name()='detail']")
                                    .string(PUBLIC_DETAIL))
                    .andExpect(content().string(not(containsString("<script>"))));
        } else {
            result.andExpect(content().string(PUBLIC_DETAIL))
                    .andExpect(
                            content()
                                    .contentTypeCompatibleWith(
                                            accept.equals("text/csv")
                                                    ? "text/csv"
                                                    : MediaType.TEXT_PLAIN_VALUE))
                    .andExpect(header().string("X-Content-Type-Options", "nosniff"));
        }
    }

    @ParameterizedTest
    @MethodSource("errorResponses")
    void errorResponsesDoNotExposeExceptionMessages(
            Exception exception, HttpStatus expectedStatus, String accept) throws Exception {
        mvcFor(exception)
                .perform(get("/error").accept(accept))
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(content().string(containsString(expectedStatus.getReasonPhrase())))
                .andExpect(content().string(not(containsString("private-error-detail"))))
                .andExpect(content().string(not(containsString("<script>"))));
    }

    @Test
    void missingParametersAreExplained() throws Exception {
        mvcFor(new IllegalStateException())
                .perform(get("/parameters").accept(MediaType.TEXT_HTML))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Required request parameter 'id'")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void invalidParametersAreExplainedWithoutRenderingHtml() throws Exception {
        mvcFor(new IllegalStateException())
                .perform(get("/parameters").param("id", PUBLIC_DETAIL).accept(MediaType.TEXT_HTML))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Method parameter 'id'")))
                .andExpect(content().string(containsString("<script>alert(\"xss\")</script>")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    private static Stream<Arguments> csvPreferences() {
        return Stream.of(
                Arguments.of("text/html;q=0.1,text/csv;q=0.9", "text/csv"),
                Arguments.of("text/csv;q=0.1,text/plain;q=0.9", "text/plain"),
                Arguments.of("text/csv;q=0,text/html", "text/plain"));
    }

    @ParameterizedTest
    @MethodSource("csvPreferences")
    void textErrorsHonorCsvPreferences(String accept, String expectedContentType) throws Exception {
        mvcFor(new ClientInputException(PUBLIC_DETAIL))
                .perform(get("/error").accept(accept))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(expectedContentType))
                .andExpect(content().string(PUBLIC_DETAIL))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    private MockMvc mvcFor(Exception exception) {
        return MockMvcBuilders.standaloneSetup(new FailingController(exception))
                .setControllerAdvice(handlers)
                .build();
    }

    private static Stream<Exception> clientDisconnects() {
        return Stream.of(
                new AsyncRequestNotUsableException("ServletOutputStream failed to write"),
                new IOException("Broken pipe"),
                new IOException("Connection reset by peer"));
    }

    @ParameterizedTest
    @MethodSource("clientDisconnects")
    void clientDisconnectsAreNotReportedAsServerFaults(Exception disconnect) throws Exception {
        assertThat(handlers.serverErrorProblemDetailExceptionHandler(disconnect, request)).isNull();
        assertThat(handlers.serverErrorTextExceptionHandler(disconnect, request)).isNull();
    }

    @Test
    void genuineServerFaultsStillProduceAProblemDetail() throws Exception {
        Exception fault = new IllegalStateException("something actually broke");

        assertThat(handlers.serverErrorProblemDetailExceptionHandler(fault, request))
                .isNotNull()
                .satisfies(
                        problem ->
                                assertThat(problem.getStatus())
                                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        assertThat(handlers.serverErrorTextExceptionHandler(fault, request))
                .isNotNull()
                .satisfies(
                        response ->
                                assertThat(response.getBody()).isEqualTo("Internal Server Error"));
    }

    @RestController
    private class FailingController {
        private final Exception exception;

        FailingController(Exception exception) {
            this.exception = exception;
        }

        @GetMapping("/error")
        public void fail() throws Exception {
            throw exception;
        }

        @GetMapping("/parameters")
        public void parameters(@RequestParam long id) {}
    }
}
