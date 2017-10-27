package ch.ethz.asl.ca.endpoint;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to answer to requests regarding user information.
 * TODO: once login is in place remove the need to pass the username in HTTP body and read user info from SecurityContext.
 */
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("user/{username}")
    public UserSafeProjection getUserDetails(@PathVariable("username") final String username) {
        Assert.isTrue(!StringUtils.isEmpty(username), "Username cannot be null.");
        return userService.getUserDetails(username);
    }

    @PostMapping("user/{username}")
    public UserSafeProjection updateUser(@RequestBody User user) {
        // Check if user is valid
        return userService.updateUser(user); // should return updated user info.
    }
}
