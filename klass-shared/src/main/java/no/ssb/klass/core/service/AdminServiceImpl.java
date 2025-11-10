package no.ssb.klass.core.service;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.ClassificationVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

  private final ClassificationSeriesRepository classificationRepository;
  private final ClassificationVariantRepository variantRepository;

  @Autowired
  public AdminServiceImpl(
      ClassificationSeriesRepository classificationRepository,
      ClassificationVariantRepository variantRepository) {
    this.classificationRepository = classificationRepository;
    this.variantRepository = variantRepository;
  }

  @Override
  public Integer getNumberOfClassificationForUser(User user) {
    return classificationRepository.getNumberOfClassificationForUser(user);
  }

  @Override
  public void updateUser(User oldUser, User newUser) {
    classificationRepository.updateUser(oldUser, newUser);
    variantRepository.updateUser(oldUser, newUser);
  }
}
