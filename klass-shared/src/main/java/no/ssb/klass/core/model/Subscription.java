package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.core.util.TimeUtil;

@Entity
public class Subscription extends BaseEntity {

  private static final int EXPIRY_TIME_IN_MINS = 60 * 24; // 24 hours
  private static final long ONE_MINUTE_IN_MILLISECS = 60000;

  @Column(nullable = false)
  private Date expiryDate;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private URL endSubscriptionUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Verification verification;

  @ManyToOne private ClassificationSeries classification;

  public Subscription() {}

  public Subscription(ClassificationSeries classification, URL endSubscriptionUrl) {
    this.classification = checkNotNull(classification);
    this.endSubscriptionUrl = checkNotNull(endSubscriptionUrl);
    this.token = UUID.randomUUID().toString();
    this.expiryDate = calculateExpiryDate();
    this.verification = Verification.NOT_VERIFIED;
  }

  String getToken() {
    return this.token;
  }

  Verification getVerification() {
    return verification;
  }

  ClassificationSeries getClassificationSeries() {
    return classification;
  }

  URL getEndSubscriptionUrl() {
    return endSubscriptionUrl;
  }

  boolean isExpired() {
    return TimeUtil.now().after(expiryDate);
  }

  private Date calculateExpiryDate() {
    long now = TimeUtil.now().getTime();
    return new Date(now + EXPIRY_TIME_IN_MINS * ONE_MINUTE_IN_MILLISECS);
  }

  Verification verify() {
    verification = isExpired() ? Verification.EXPIRED : Verification.VALID;
    if (verification == Verification.EXPIRED) {
      throw new ClientException("Verification code is expired.");
    }
    return verification;
  }
}
