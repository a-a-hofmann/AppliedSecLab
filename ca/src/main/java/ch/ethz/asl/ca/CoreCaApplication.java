package ch.ethz.asl.ca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
//@EnableResourceServer // TODO: Add back in to use oauth2
public class CoreCaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreCaApplication.class, args);
    }
}