package ch.ethz.asl.ca.endpoint;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.security.AuthenticatedUserPrincipal;
import ch.ethz.asl.ca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Controller to answer to requests regarding user information.
 */
@RestController
public class UserController {

    private final UserService userService;

    private final CredentialsParser credentialsParser;

    @Autowired
    public UserController(UserService userService, CredentialsParser credentialsParser) {
        this.userService = userService;
        this.credentialsParser = credentialsParser;
    }

    @GetMapping("user")
    public UserSafeProjection getUserDetails(Principal principal) {
        Assert.isTrue(!StringUtils.isEmpty(principal), "No principal found in SecurityContext.");
        return userService.getUserDetails(principal.getName());
    }

    @PostMapping("user")
    public UserSafeProjection updateUser(@RequestBody User user, Principal principal) {
        // load user from context for update.
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        AuthenticatedUserPrincipal loggedInUser = (AuthenticatedUserPrincipal) authenticationToken.getPrincipal();

        if (StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(loggedInUser.getPassword());
        }
        return userService.updateUser(user); // should return updated user info.
    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity<Void> principal(HttpServletRequest request) {
        ResponseEntity<Void> badRequest = ResponseEntity.badRequest().build();

        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            return badRequest;
        }

        CredentialsParser.Credentials userCredentials = credentialsParser.parseHeaderForCredentials(authorization);
        boolean valid = userService.checkUserCredentials(userCredentials);

        if (valid) {
            return ResponseEntity.ok().build();
        }
        return badRequest;
    }
}
