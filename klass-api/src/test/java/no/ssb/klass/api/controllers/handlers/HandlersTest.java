package no.ssb.klass.api.controllers.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.io.IOException;
import java.util.stream.Stream;

class HandlersTest {

    private final Handlers handlers = new Handlers();
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private static Stream<Exception> clientDisconnects() {
        return Stream.of(
                new AsyncRequestNotUsableException("ServletOutputStream failed to write"),
                new IOException("Broken pipe"),
                new IOException("Connection reset by peer"));
    }

    @ParameterizedTest
    @MethodSource("clientDisconnects")
    void clientDisconnectsAreNotReportedAsServerFaults(Exception disconnect) {
        assertThat(handlers.serverErrorProblemDetailExceptionHandler(disconnect, request)).isNull();
        assertThat(handlers.serverErrorTextExceptionHandler(disconnect, request)).isNull();
    }

    @Test
    void genuineServerFaultsStillProduceAProblemDetail() {
        Exception fault = new IllegalStateException("something actually broke");

        assertThat(handlers.serverErrorProblemDetailExceptionHandler(fault, request))
                .isNotNull()
                .satisfies(
                        problem ->
                                assertThat(problem.getStatus())
                                        .isEqualTo(HttpStatus.BAD_REQUEST.value()));
        assertThat(handlers.serverErrorTextExceptionHandler(fault, request))
                .isEqualTo("something actually broke");
    }
}
