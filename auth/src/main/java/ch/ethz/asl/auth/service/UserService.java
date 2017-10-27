package ch.ethz.asl.auth.service;

import ch.ethz.asl.auth.model.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getUserDetails() {
        return null;
    }
}
