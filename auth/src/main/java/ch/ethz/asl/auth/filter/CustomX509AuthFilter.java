package ch.ethz.asl.auth.filter;

import ch.ethz.asl.auth.config.AuthenticationApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

public class CustomX509AuthFilter extends X509AuthenticationFilter {

    private X509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();

    private boolean initialized;

    private HttpSecurity httpSecurity;

    private final AuthenticationApi authenticationApi;

    private final String adminEmail;

    public CustomX509AuthFilter(HttpSecurity http, AuthenticationApi authenticationApi, String adminEmail) throws Exception {
        this.httpSecurity = http;
        this.authenticationApi = authenticationApi;
        this.adminEmail = adminEmail;
    }

    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        X509Certificate cert = extractClientCertificate(request);

        if (cert == null) {
            return null;
        }

        String emailAddress = (String) principalExtractor.extractPrincipal(cert);
        BigInteger serialNumber = cert.getSerialNumber();
        String serialNrString = Long.toHexString(serialNumber.longValue());

        // Admin override.
        if (adminEmail.equals(emailAddress)) {
            return "admin";
        }

        ResponseEntity<String> response = authenticationApi.verifySerialNrAndEmail(serialNrString, emailAddress);
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
