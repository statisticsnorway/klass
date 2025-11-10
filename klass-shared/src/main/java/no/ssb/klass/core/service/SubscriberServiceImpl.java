package no.ssb.klass.core.service;

import java.net.URL;
import java.util.Optional;
import no.ssb.klass.core.exception.KlassEmailException;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Subscriber;
import no.ssb.klass.core.model.Verification;
import no.ssb.klass.core.repository.SubscriberRepository;
import no.ssb.klass.core.util.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriberServiceImpl implements SubscriberService {
  private static final Logger log = LoggerFactory.getLogger(SubscriberServiceImpl.class);

  private final SubscriberRepository subscriberRepository;
  private final MailService mailService;

  @Autowired
  public SubscriberServiceImpl(SubscriberRepository subscriberRepository, MailService mailService) {
    this.subscriberRepository = subscriberRepository;
    this.mailService = mailService;
  }

  @Override
  public String trackChanges(
      String email, ClassificationSeries classification, URL endSubscriptionUrl) {
    Optional<Subscriber> opt = subscriberRepository.findOneByEmail(email);
    Subscriber subscriber = opt.orElse(new Subscriber(email));
    String token = subscriber.addSubscription(classification, endSubscriptionUrl);
    subscriberRepository.save(subscriber);
    return token;
  }

  @Override
  public boolean removeTracking(String email, ClassificationSeries classification) {
    Subscriber subscriber = findByEmail(email);
    boolean removed = subscriber.removeSubscription(classification);
    subscriberRepository.save(subscriber);
    return removed;
  }

  @Override
  public void sendVerificationMail(
      String email, URL verifySubscriptionUrl, ClassificationSeries classification)
      throws KlassEmailException {
    String heading = "Verifiser abonnement på klassifikasjon";
    String body =
        "<br/><br/>Verifiser ditt abonnement på klassifikasjonen  <em>"
            + classification.getNameInPrimaryLanguage()
            + "</em>";
    body +=
        "<br/><br/>Klikk følgende lenke innen 24 timer: <a href=\""
            + verifySubscriptionUrl.toString()
            + "\">Verifiser abonnement</a>";
    try {
      mailService.sendMail(email, heading, body);
    } catch (Exception e) {
      throw new KlassEmailException("Unable to send verification mail", e);
    }
  }

  @Override
  public Verification verifyTracking(String email, String token) {
    Subscriber subscriber = findByEmail(email);
    return subscriber.verify(token);
  }

  private Subscriber findByEmail(String email) {
    Optional<Subscriber> opt = subscriberRepository.findOneByEmail(email);
    return opt.orElseThrow(() -> new ClientException("Unknown email address: " + email));
  }

  @Override
  public void informSubscribersOfUpdatedClassification(
      ClassificationSeries classification, String whatChanged, String descriptionOfChange) {
    for (Subscriber subscriber :
        subscriberRepository.findVerifiedSubscribersOfClassification(classification)) {
      sendMailToSubscriber(classification, whatChanged, descriptionOfChange, subscriber);
    }
  }

  @Override
  public boolean containsTracking(String email, ClassificationSeries classification) {
    Optional<Subscriber> subscriber = subscriberRepository.findOneByEmail(email);
    return subscriber
        .map(subscriber1 -> subscriber1.subscriptionExist(classification))
        .orElse(false);
  }

  private void sendMailToSubscriber(
      ClassificationSeries classification,
      String whatChanged,
      String reasonForChange,
      Subscriber subscriber) {
    String mailSubject = classification.getNameInPrimaryLanguage() + " har blitt oppdatert";
    String reasonForChangeParagraph =
        "<p>Grunn for endring<br/>-----------------------<pre>" + reasonForChange + "</pre></p>";
    String signatureParagraph = "<br/>Melding generert av SSBs kodeverkssystem";
    String unsubscribeParagraph =
        "<p><small>For å avslutte abonnement klikk følgende lenke: <a href=\""
            + subscriber.getEndSubscriptionUrl(classification)
            + "\">Avslutt abonnement</a></small></p>";
    String mailBody =
        whatChanged + reasonForChangeParagraph + signatureParagraph + unsubscribeParagraph;
    try {
      mailService.sendMail(subscriber.getEmail(), mailSubject, mailBody);
    } catch (Exception e) {
      log.error(
          "Failed to send email to subscriber: "
              + subscriber.getEmail()
              + ", for classification: "
              + classification.getNameInPrimaryLanguage()
              + "\nMail subject: "
              + mailSubject
              + "\nMail body:\n"
              + mailBody,
          e);
    }
  }
}
