package ch.ethz.asl.ca.service.command;

/**
 * The CertificateManagerExcpetion occurs when a command doesn't exist or it isn't used properly.
 * 
 * @version 1
 */
public class CertificateManagerException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CertificateManagerException instance.
     */
    public CertificateManagerException() {
        super();
    }

    /**
     * Constructs a new CertificateManagerException instance with an argument indicating the
     * exception cause.
     * 
     * @param message The message indicating the problem.
     */
    public CertificateManagerException(String message) {
        super(message);
    }

    /**
     * Constructs a new CertificateManagerException instance with an argument indicating the
     * exception cause.
     * 
     * @param cause The message indicating the cause of the problem.
     */
    public CertificateManagerException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new CertificateManagerException instance with an argument indicating the
     * exception and its cause.
     * 
     * @param cause The message indicating the cause of the problem.
     * @param message The message indicating the problem.
     */
    public CertificateManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
