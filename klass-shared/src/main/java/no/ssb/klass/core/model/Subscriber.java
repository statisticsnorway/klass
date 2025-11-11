package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Lists;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import no.ssb.klass.core.util.ClientException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(indexes = @Index(columnList = "email", name = "subscriber_email_idx", unique = true))
public class Subscriber extends BaseEntity {

    private static final Logger log = LoggerFactory.getLogger(Subscriber.class);

    @Column(nullable = false)
    private final String email;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "subscriber")
    private final List<Subscription> subscriptions;

    public Subscriber(String email) {
        this.email = checkNotNull(email);
        this.subscriptions = Lists.newArrayList();
    }

    public String addSubscription(ClassificationSeries classification, URL endSubscriptionUrl) {
        log.debug("Add subscription: " + classification.getId());
        checkNotNull(classification);
        checkNotNull(endSubscriptionUrl);
        if (subscriptionExist(classification)) {
            throw new ClientException(
                    "It is previously registered a subscription for classification: "
                            + classification.getId()
                            + " "
                            + classification.getName(Language.getDefault()));
        }
        Subscription subscription = new Subscription(classification, endSubscriptionUrl);
        subscriptions.add(subscription);
        return subscription.getToken();
    }

    public boolean removeSubscription(ClassificationSeries classification) {
        log.debug("Remove subscription: " + classification.getId());
        checkNotNull(classification);
        boolean deleted =
                subscriptions.removeIf(
                        t ->
                                Objects.equals(
                                        t.getClassificationSeries().getId(),
                                        classification.getId()));
        if (!deleted) {
            throw new ClientException(
                    "Could not find any subscription for classification: "
                            + classification.getId()
                            + " "
                            + classification.getName(Language.getDefault()));
        }
        return deleted;
    }

    public String getEndSubscriptionUrl(ClassificationSeries classification) {
        return subscriptions.stream()
                .filter(
                        s ->
                                Objects.equals(
                                        s.getClassificationSeries().getId(),
                                        classification.getId()))
                .findAny()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No subscription for classification: "
                                                + classification.getId()))
                .getEndSubscriptionUrl()
                .toString();
    }

    public boolean subscriptionExist(ClassificationSeries classification) {
        return subscriptions.stream()
                .anyMatch(
                        t ->
                                Objects.equals(
                                        t.getClassificationSeries().getId(),
                                        classification.getId()));
    }

    private Optional<Subscription> getSubscriptionWithToken(String token) {
        checkNotNull(token);
        return subscriptions.stream().filter(v -> v.getToken().equals(token)).findFirst();
    }

    public Verification verify(String token) {
        log.debug("Verify subscription with token: " + token);
        checkNotNull(token);
        Optional<Subscription> opt = getSubscriptionWithToken(token);
        Subscription subscription =
                opt.orElseThrow(() -> new ClientException("Invalid verification code."));
        return subscription.verify();
    }

    public String getEmail() {
        return email;
    }
}
