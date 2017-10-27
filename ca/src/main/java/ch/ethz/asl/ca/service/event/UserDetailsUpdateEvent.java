package ch.ethz.asl.ca.service.event;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserSafeProjection;

/**
 * Represents the update of user info.
 * Uses UserSafeProjection to prevent the pwd leaking in case we log this event.
 * TODO maybe just save the diffs.
 */
public class UserDetailsUpdateEvent extends Event {

    private final UserSafeProjection before;

    private final UserSafeProjection after;

    private final boolean isRevokeCerts;

    public UserDetailsUpdateEvent(String user, User before, User after, boolean isRevokeCerts) {
        super(user);
        this.before = UserSafeProjection.of(before);
        this.after = UserSafeProjection.of(after);
        this.isRevokeCerts = isRevokeCerts;
    }

    public UserSafeProjection getBefore() {
        return before;
    }

    public UserSafeProjection getAfter() {
        return after;
    }

    public boolean isRevokeCerts() {
        return isRevokeCerts;
    }
}
