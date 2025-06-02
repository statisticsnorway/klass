package no.ssb.klass.core.service;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.getUserIdsForUsersWithClassifications().size();
    }

    @Override
    public List<User> getUsersWithClassifications() {
        Set<BigInteger> userIds = userRepository.getUserIdsForUsersWithClassifications();
        List<Long> collect = userIds.stream().map(bigInteger -> bigInteger.longValueExact()).collect(Collectors
                .toList());
        return userRepository.findAll(collect);

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
