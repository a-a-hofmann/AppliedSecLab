package ch.ethz.asl.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationApi authenticationApi;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .formLogin().loginPage("/login").permitAll()
                .and()
                .requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access")
                .and()
                .authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    public RestAuthenticationProvider authenticationProvider() {
        RestAuthenticationProvider authProvider
                = new RestAuthenticationProvider();
        authProvider.setAuthenticationApi(authenticationApi);
        return authProvider;
    }
}
