package no.ssb.klass.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    
    private final ClassificationSeriesRepository classificationRepository;
    
    @Autowired
    public AdminServiceImpl(ClassificationSeriesRepository classificationRepository) {
        this.classificationRepository = classificationRepository;       
    }
    
    @Override
    public Integer getNumberOfClassificationForUser(User user) {        
        return classificationRepository.getNumberOfClassificationForUser(user);
    }

    @Override
    public void updateUser(User oldUser, User newUser) {
        classificationRepository.updateUser(oldUser, newUser);
    }
}
