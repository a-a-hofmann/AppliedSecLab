package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.service.event.UserEventListener;
import ch.ethz.asl.ca.service.event.UserInfoRequestEvent;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserEventListener eventListener;

    public UserService(UserRepository userRepository, UserEventListener eventListener) {
        this.userRepository = userRepository;
        this.eventListener = eventListener;
    }

    public UserSafeProjection getUserDetails(final String username) {
        UserSafeProjection userInfo = userRepository.findByUsername(username);
        eventListener.onUserInfoRequest(new UserInfoRequestEvent(username, userInfo));
        return userInfo;
    }

    public UserSafeProjection updateUser(final User user) {
        return UserSafeProjection.of(user);
    }
}