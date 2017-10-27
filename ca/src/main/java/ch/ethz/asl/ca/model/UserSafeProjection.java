package ch.ethz.asl.ca.model;

import java.io.Serializable;

public class UserSafeProjection implements Serializable {

    private final String username;

    private final String lastname;

    private final String firstname;

    private final String email;

    public UserSafeProjection(String username, String lastname, String firstname, String email) {
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public static UserSafeProjection of(final User user) {
        return new UserSafeProjection(user.getUsername(), user.getLastname(), user.getFirstname(), user.getEmail());
    }

    @Override
    public String toString() {
        return "UserSafeProjection{" +
                "lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
