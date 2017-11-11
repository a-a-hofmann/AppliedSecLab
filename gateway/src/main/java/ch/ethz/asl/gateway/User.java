package ch.ethz.asl.gateway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class User implements Serializable {

    private String username;

    @NotNull
    @Size(min = 1)
    private String lastname;

    @NotNull
    @Size(min = 1)
    private String firstname;

    @NotNull
    @Size(min = 4)
    private String email;

    private String password;

    private String passwordConfirmation;

    public User() {
    }

    @JsonCreator
    public User(@JsonProperty("username") String username, @JsonProperty("lastname") String lastname,
                @JsonProperty("firstname") String firstname, @JsonProperty("email") String email,
                @JsonProperty("password") String password) {
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    @AssertTrue(message = "The password and password confirmation are not the same.")
    private boolean isPasswordOk() {
        return password == null || (password.equals(passwordConfirmation));
    }

    @Override
    public String toString() {
        return "User{" +
                "lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
