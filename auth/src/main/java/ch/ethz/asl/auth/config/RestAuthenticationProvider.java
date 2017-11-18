package ch.ethz.asl.auth.config;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class RestAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final Logger logger = Logger.getLogger(RestAuthenticationProvider.class);

    private AuthenticationApi authenticationApi;

    public void setAuthenticationApi(AuthenticationApi authenticationApi) {
        this.authenticationApi = authenticationApi;
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    protected final UserDetails retrieveUser(String username,
                                             UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String password = authentication.getCredentials().toString();

        ResponseEntity<?> authenticationResponse = authenticationApi.authenticate(username, password);

        CurrentRequest request = CurrentRequest.getCurrentHttpRequest();

        if (authenticationResponse.getStatusCode().is4xxClientError() && !request.isFlagSet()) {
            throw new AuthenticationServiceException("Bad credentials");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(username, password, authorities);
    }
}