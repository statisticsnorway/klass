package no.ssb.klass.core.model;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.testutil.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubscriberTest {

  static final String EMAIL = "email@host.com";
  static final String TOKEN = "token";
  static final String NAME = "name";
  static final String END_SUBSCRIPTION_URL = "http://test.url";
  private Subscriber subject;

  private ClassificationSeries classification;

  @BeforeEach
  public void init() throws Exception {
    subject = new Subscriber(EMAIL);
    classification = TestUtil.createClassificationWithId(1, NAME);
    subject.addSubscription(classification, new URL(END_SUBSCRIPTION_URL));
  }

  @Test
  public void addSubscription() throws Exception {
    // when
    ClassificationSeries classification = TestUtil.createClassification(NAME);
    String result = subject.addSubscription(classification, new URL("http://test.url"));
    Verification verification = subject.verify(result);
    // then
    assertEquals(Verification.VALID, verification);
  }

  @Test
  public void addSubscriptionExist() {
    // when

    ClassificationSeries classification = TestUtil.createClassificationWithId(1, NAME);
    Assertions.assertThrows(
        ClientException.class,
        () -> subject.addSubscription(classification, new URL("http://test.url")));
    // then exception
  }

  @Test
  public void addSubscriptionNull() {
    // when
    Assertions.assertThrows(
        NullPointerException.class,
        () -> subject.addSubscription(null, new URL("http://test.url")));
    // then exception
  }

  @Test
  public void addEndSubscriptionNull() {
    // when
    Assertions.assertThrows(
        NullPointerException.class, () -> subject.addSubscription(classification, null));
    // then exception
  }

  @Test
  public void removeSubscription() {
    // when
    boolean result = subject.removeSubscription(classification);
    // then
    assertEquals(true, result);
  }

  @Test
  public void getEndSubscriptionUrl() {
    // when
    String result = subject.getEndSubscriptionUrl(classification);
    // then
    assertEquals(END_SUBSCRIPTION_URL, result);
  }

  @Test
  public void removeSubscriptionNotFound() {
    // when
    Assertions.assertThrows(
        ClientException.class,
        () -> subject.removeSubscription(TestUtil.createClassification(NAME)));
    // then exception
  }

  @Test
  public void verifyNull() {
    // when
    Assertions.assertThrows(NullPointerException.class, () -> subject.verify(null));
    // then exception
  }

  @Test
  public void verifyNotFound() {
    // when
    Assertions.assertThrows(ClientException.class, () -> subject.verify(TOKEN));
    // then exception
  }
}
