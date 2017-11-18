package ch.ethz.asl.auth.config;

import ch.ethz.asl.auth.filter.CustomX509AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;

import java.util.List;

@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationApi authenticationApi;

    @Value("${ca.admin.email}")
    private String adminEmail;

    @Autowired
    public SecurityConfig(AuthenticationApi authenticationApi) {
        this.authenticationApi = authenticationApi;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomX509AuthFilter x509AuthFilter = new CustomX509AuthFilter(http, authenticationApi, adminEmail);
        SubjectDnX509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();
        principalExtractor.setSubjectDnRegex("EMAILADDRESS=(.*?)(?:,|$)");
        x509AuthFilter.setPrincipalExtractor(principalExtractor);

        http
                .authenticationProvider(authenticationProvider())
                .formLogin().loginPage("/login").permitAll()
                .and()
                .x509()
                .x509AuthenticationFilter(x509AuthFilter).userDetailsService(userDetailsService())
                .and()
                .requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access")
                .and()
                .authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public RestAuthenticationProvider authenticationProvider() {
        RestAuthenticationProvider authProvider
                = new RestAuthenticationProvider();
        authProvider.setAuthenticationApi(authenticationApi);
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            List<GrantedAuthority> authorities;
            if (username.equals("admin")) {
                authorities = AuthorityUtils
                        .commaSeparatedStringToAuthorityList("ROLE_ADMIN, ROLE_CERT");
            } else {
                authorities = AuthorityUtils
                        .commaSeparatedStringToAuthorityList("ROLE_USER, ROLE_CERT");
            }

            return new User(username, "", authorities);
        };
    }
}
