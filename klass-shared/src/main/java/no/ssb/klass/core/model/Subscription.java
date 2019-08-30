package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.core.util.TimeUtil;

@Entity
public class Subscription extends BaseEntity {

    private static final int EXPIRY_TIME_IN_MINS = 60 * 24; // 24 hours
    private static final long ONE_MINUTE_IN_MILLISECS = 60000;

    @Column(nullable = false)
    private final Date expiryDate;
    @Column(nullable = false)
    private final String token;
    @Column(nullable = false)
    private final URL endSubscriptionUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Verification verification;

    @ManyToOne
    private final ClassificationSeries classification;

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