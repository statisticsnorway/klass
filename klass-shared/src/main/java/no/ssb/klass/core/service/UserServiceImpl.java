package no.ssb.klass.core.service;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClassificationService classificationService;

    @Autowired
    UserServiceImpl(UserRepository userRepository, ClassificationService classificationService) {
        this.userRepository = userRepository;
        this.classificationService = classificationService;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public long getUsersCount() {
        return userRepository.count();
    }

    @Override
    public long countUsersWithClassifications() {
        return getUsersWithClassifications().size();
    }

    @Override
    public List<User> getUsersWithClassifications() {
        return userRepository.findAll().stream()
                .filter(classificationService::doesUserOwnAnyClassifications)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserByFullName(String fullName) {
        return initializeFavorites(userRepository.findOneByFullname(fullName));
    }

    @Override
    public User getUserByUserName(String userName) {
        return initializeFavorites(userRepository.findOneByUsername(userName));
    }

    @Override
    public User getUserById(long userId) {
        return initializeFavorites(userRepository.findOne(userId));
    }

    private User initializeFavorites(User user) {
        if (user == null) {
            return null;
        }
        Hibernate.initialize(user.getFavorites());
        return user;
    }
}
