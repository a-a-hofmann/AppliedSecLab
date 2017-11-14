package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.endpoint.CredentialsParser;
import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.service.event.UserDetailsUpdateEvent;
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

    public void updateUser(User updatedInfo, final String username) {
        User userRequestingUpdate = userRepository.findOne(username);

        boolean requiresCertRevocation = userRequestingUpdate.updateRequiresCertRevocation(updatedInfo);
        UserDetailsUpdateEvent updateEvent =
                new UserDetailsUpdateEvent(username, userRequestingUpdate, updatedInfo, requiresCertRevocation);
        eventListener.onUserInfoUpdate(updateEvent);

        userRequestingUpdate.update(updatedInfo);
        userRepository.save(userRequestingUpdate);
    }

    public boolean checkUserCredentials(CredentialsParser.Credentials credentials) {
        User user = this.userRepository.findOne(credentials.getUsername());
        return user != null && user.getPassword().equals(credentials.getPassword());
    }

    public UserSafeProjection findUsernameByEmail(final String email) {
        return this.userRepository.findByEmail(email);
    }
}
