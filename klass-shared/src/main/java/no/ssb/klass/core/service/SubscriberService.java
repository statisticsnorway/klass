package no.ssb.klass.core.service;

import java.net.URL;
import no.ssb.klass.core.exception.KlassEmailException;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Verification;

public interface SubscriberService {

  String trackChanges(String email, ClassificationSeries classification, URL endSubscriptionUrl);

  boolean removeTracking(String email, ClassificationSeries classification);

  void sendVerificationMail(
      String email, URL verifySubscriptionUrl, ClassificationSeries classification)
      throws KlassEmailException;

  Verification verifyTracking(String email, String token);

  void informSubscribersOfUpdatedClassification(
      ClassificationSeries classification, String whatChanged, String descriptionOfChange);

  boolean containsTracking(String email, ClassificationSeries classification);
}
