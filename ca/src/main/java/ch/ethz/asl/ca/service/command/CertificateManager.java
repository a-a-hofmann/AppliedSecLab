
package ch.ethz.asl.ca.service.command;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserCertificate;

import javax.servlet.ServletOutputStream;
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
     * @return the serialNr of the issued certificate.
     * @throws CertificateManagerException
     */
    public String issueNewCertificate(final User user) throws CertificateManagerException;

    /**
     * This method writes the certificate in the outputStream and returns the metadata stored in the mySQL database.
     *
     * @return true, if the certificate of the user with the serial number exists
     * @throws CertificateManagerException
     */
    public boolean getCertificate(final String serialNr, final User user, ServletOutputStream outputStream) throws CertificateManagerException;

    /**
     * This method revokes a certificate with the serial number serialNr of the user.
     *
     * @return true, if a valid certificate of he user with the serial number exists.
     * @throws CertificateManagerException
     */
    public boolean revokeCertificate(final String serialNr, final User user) throws CertificateManagerException;


    /**
     * @return the number of issued certificates.
     * @throws CertificateManagerException
     */
    public Long getNumberOfIssuedCertificates() throws CertificateManagerException;

    /**
     * @return the number of revoked certificates.
     * @throws CertificateManagerException
     */
    public Long getNumberOfRevokedCertificates() throws CertificateManagerException;

    /**
     * @return the current serial number.
     * @throws CertificateManagerException
     */
    public String getCurrentSerialNumber() throws CertificateManagerException;

    /**
     *@return the path of the user with the serial number if it exists, otherwise null
     */
    public String getPath(final String serialNr, final User user);
}
