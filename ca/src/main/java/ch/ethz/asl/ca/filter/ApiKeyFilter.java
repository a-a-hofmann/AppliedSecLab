package ch.ethz.asl.ca.filter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApiKeyFilter extends AbstractAuthenticationProcessingFilter {

    private static final String SHARED_SECRET_HEADER = "X-Authorization";

    @Value("${client-secret}")
    private String sharedSecret;

    private static final Logger logger = Logger.getLogger(ApiKeyFilter.class);

    public ApiKeyFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/login", "POST"));
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String pathInfo = ((HttpServletRequest) request).getServletPath();
        if (pathInfo.contains("/authenticate")) { // Otherwise just delegate to OAuth2
            attemptAuthentication(httpRequest, (HttpServletResponse) response);
        }
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String apiKey = request.getHeader(SHARED_SECRET_HEADER);
        if (apiKey == null || !apiKey.equals(sharedSecret)) {
            logger.error("Missing api key!");
            throw new BadCredentialsException("Bad credentials");
        }
        return null;
    }
}
