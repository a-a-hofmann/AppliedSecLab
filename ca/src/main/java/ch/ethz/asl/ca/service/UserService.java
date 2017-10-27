package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.model.UserSafeProjection;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserSafeProjection getUserDetails(final String username) {
        return userRepository.findByUsername(username);
    }

    public UserSafeProjection updateUser(final User user) {
        return UserSafeProjection.of(user);
    }
}
