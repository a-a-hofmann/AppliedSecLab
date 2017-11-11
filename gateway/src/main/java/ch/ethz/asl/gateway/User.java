package ch.ethz.asl.gateway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class User implements Serializable {

    private final String username;

    private final String lastname;

    private final String firstname;

    private final String email;

    @JsonCreator
    public User(@JsonProperty("username") String username, @JsonProperty("lastname") String lastname,
                @JsonProperty("firstname") String firstname, @JsonProperty("email") String email) {
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

    @Override
    public String toString() {
        return "UserSafeProjection{" +
                "lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
