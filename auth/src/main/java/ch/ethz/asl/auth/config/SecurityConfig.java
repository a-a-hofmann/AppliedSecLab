package ch.ethz.asl.auth.config;

import ch.ethz.asl.auth.filter.CRLValidityService;
import ch.ethz.asl.auth.filter.CustomX509AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;

@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationApi authenticationApi;

    private final CRLValidityService crlValidityService;

    @Autowired
    public SecurityConfig(AuthenticationApi authenticationApi, CRLValidityService crlValidityService) {
        this.authenticationApi = authenticationApi;
        this.crlValidityService = crlValidityService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomX509AuthFilter x509AuthFilter = new CustomX509AuthFilter(http, crlValidityService, authenticationApi);
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

    @Bean
    public RestAuthenticationProvider authenticationProvider() {
        RestAuthenticationProvider authProvider
                = new RestAuthenticationProvider();
        authProvider.setAuthenticationApi(authenticationApi);
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new User(username, "",
                AuthorityUtils
                        .commaSeparatedStringToAuthorityList("ROLE_USER, ROLE_CERT"));
    }
}
