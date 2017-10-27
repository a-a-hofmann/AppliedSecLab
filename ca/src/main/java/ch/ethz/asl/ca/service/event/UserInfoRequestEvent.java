package ch.ethz.asl.ca.service.event;

import ch.ethz.asl.ca.model.UserSafeProjection;

/**
 * Represents a user requesting to view his info.
 * Uses UserSafeProjection to prevent the pwd hash leaking in case we log this event.
 */
public class UserInfoRequestEvent extends Event {

    private final UserSafeProjection userThatWasRequested;

    public UserInfoRequestEvent(String user, UserSafeProjection userThatWasRequested) {
        super(user);
        this.userThatWasRequested = userThatWasRequested;
    }

    public UserSafeProjection getUserThatWasRequested() {
        return userThatWasRequested;
    }
}
