package ch.ethz.asl.auth.endpoint;

import ch.ethz.asl.auth.service.UserDto;
import ch.ethz.asl.auth.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("user")
    public UserDto getUserDetails(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        logger.info(usernamePasswordAuthenticationToken);
        return null;
    }
}
