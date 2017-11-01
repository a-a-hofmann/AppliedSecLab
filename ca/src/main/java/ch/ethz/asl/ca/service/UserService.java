package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.service.event.UserEventListener;
import ch.ethz.asl.ca.service.event.UserInfoRequestEvent;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
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

    public UserSafeProjection updateUser(User user) {
        return UserSafeProjection.of(user);
    }

    private void passwordUpdate(User user) {
        String password = new ShaPasswordEncoder().encodePassword(user.getPassword(), null);
        user.setPassword(password);
    }
}
