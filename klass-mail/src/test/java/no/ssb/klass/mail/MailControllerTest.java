package no.ssb.klass.mail;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ssb.klass.mail.controllers.MailController;
import no.ssb.klass.mail.models.Email;
import no.ssb.klass.mail.services.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
    value = MailController.class,
    properties = {
      "spring.cloud.gcp.project-id=dummy",
      "spring.cloud.gcp.core.enabled=false",
      "spring.cloud.gcp.config.enabled=false",
      "spring.cloud.gcp.pubsub.enabled=false"
    })
public class MailControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private MailService testMailService;

  private final ObjectMapper mapper = new ObjectMapper();

  private static final Email testEmail = new Email("me@example.com", "Test", "Just testing");

  @Test
  public void sendMail() throws Exception {

    mvc.perform(
            post("/mail")
                .content(mapper.writeValueAsString(testEmail))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    testMailService.sendMail(testEmail);
  }

  @Configuration
  @Import(MailController.class) // A @Component injected with ExampleService
  static class Config {}
}
