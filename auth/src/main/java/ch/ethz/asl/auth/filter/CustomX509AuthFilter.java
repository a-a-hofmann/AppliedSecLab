package ch.ethz.asl.auth.filter;

import ch.ethz.asl.auth.config.AuthenticationApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;

public class CustomX509AuthFilter extends X509AuthenticationFilter {

    private X509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();

    private boolean initialized;

    private HttpSecurity httpSecurity;

    private final AuthenticationApi authenticationApi;

    public CustomX509AuthFilter(HttpSecurity http, AuthenticationApi authenticationApi) throws Exception {
        this.httpSecurity = http;
        this.authenticationApi = authenticationApi;
    }

    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        X509Certificate cert = extractClientCertificate(request);

        if (cert == null) {
            return null;
        }

        Object o = principalExtractor.extractPrincipal(cert);
        ResponseEntity<String> response = authenticationApi.verifySerialNrAndEmail(cert.getSerialNumber().toString(16), (String) o);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return null;
        }
        return response.getBody();
    }

    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        if (!initialized) {
            AuthenticationManager authenticationManager = httpSecurity.getSharedObject(AuthenticationManager.class);
            setAuthenticationManager(authenticationManager);
            initialized = !initialized;
        }
        return extractClientCertificate(request);
    }

    private X509Certificate extractClientCertificate(HttpServletRequest request) {
        X509Certificate[] certs = (X509Certificate[]) request
                .getAttribute("javax.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("X.509 client authentication certificate:" + certs[0]);
            }

            return certs[0];
        }

        if (logger.isDebugEnabled()) {
            logger.debug("No client certificate found in request.");
        }

        return null;
    }

    public void setPrincipalExtractor(X509PrincipalExtractor principalExtractor) {
        this.principalExtractor = principalExtractor;
    }
}
