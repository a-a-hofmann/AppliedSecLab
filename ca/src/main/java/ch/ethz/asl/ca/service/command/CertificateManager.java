
package ch.ethz.asl.ca.service.command;

import ch.ethz.asl.ca.model.User;

import java.security.cert.X509Certificate;

/**
 * Defines an interface <tt>CertificateManager</tt> which is used to manage the certificate authority.
 * <p>
 * Users of this interface must implement the <code>issueNewCertificate</code>, <code>getCertificate</code> and <code>revokeCertificate</code> method.
 * </p>
 *
 * @version 1
 */
public interface CertificateManager {

    /**
     * This method issues a new certificate in the CA repository.
     *
     * @param user to whom we issue a certificate.
     * @return certificate serial number.
     * @throws CertificateManagerException
     */
    public String issueNewCertificate(final User user) throws CertificateManagerException;

    /**
     * This method returns a certificate if a certificate with the serialNr exists, otherwise it returns null.
     *
     * @return X509Certificate
     */
    public X509Certificate getCertificate(final String serialNr, final User user) throws CertificateManagerException;

    /**
     * This method revokes a certificate with the serial number serialNr.
     */
    public void revokeCertificate(final String serialNr, final User user) throws CertificateManagerException;

}
