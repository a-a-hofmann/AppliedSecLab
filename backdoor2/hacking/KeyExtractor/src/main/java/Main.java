import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * This project is created to extract the private key of the CA certificate, if the CA doesn't sign other certificate properly.
 * This is the entry point of the program.
 */
public class Main {

    private static final String ERROR_ARGUMENTS = "Error, %d arguments are not allowed! Please give the path of the two issued certificates in the PEM format when you start the program.";
    private static final String ERROR = "Error, could not parse the file!";
    private static final String DSA_Q = "0086c32e9755a7508d898b66fa6bbc809ff2d52e6b31a3302051cd728cbb434cf7";


    public static void main(String... args) {
        try {
            checkTheArguments(args, 2);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        X509Certificate certificate1 = null;
        X509Certificate certificate2 = null;
        try {
            certificate1 = generateX509Certiticate(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("The first file couldn't be found.");
            System.exit(1);
            //e.printStackTrace();
        } catch (CertificateException e) {
            System.out.println("The certificate couldn't be generated from this file " + args[0]);
            //e.printStackTrace();
            System.exit(1);
        }

        try {
            certificate2 = generateX509Certiticate(args[1]);
        } catch (FileNotFoundException e) {
            System.out.println("The second file couldn't be found.");
            //e.printStackTrace();
            System.exit(1);
        } catch (CertificateException e) {
            System.out.println("The certificate couldn't be generated from this file " + args[1]);
            //e.printStackTrace();
            System.exit(1);
        }


        Digest digest1 = new Digest(certificate1);
        Digest digest2 = new Digest(certificate2);

        String hash1 = null;
        String signatureS1 = null;
        String signatureR1 = null;
        try {
            hash1 = digest1.getDigest();
            signatureS1 = digest1.getS();
            signatureR1 = digest1.getR();
            System.out.println("The first digest is: " + hash1);
            System.out.println("S is " + signatureS1);
            System.out.println("R is " + signatureR1);
        } catch (IOException|CertificateException e) {
            System.out.println("An error occured while the first digest was being calculated. ");
            e.printStackTrace();
            System.exit(1);
        }


        String hash2 = null;
        String signatureS2 = null;
        String signatureR2 = null;
        try {
            hash2 = digest2.getDigest();
            signatureS2 = digest2.getS();
            signatureR2 = digest2.getR();
            System.out.println("The second digest is: " + hash2);
            System.out.println("S is " + signatureS2);
            System.out.println("R is " + signatureR2);
        } catch (IOException|CertificateException e) {
            System.out.println("An error occured while the second digest was being calculated. ");
            e.printStackTrace();
            System.exit(1);
        }

        BigInteger d1 = new BigInteger(hash1, 16);
        BigInteger d2 = new BigInteger(hash2, 16);
        BigInteger s1 = new BigInteger(signatureS1, 16);
        BigInteger s2 = new BigInteger(signatureS2, 16);
        BigInteger r1 = new BigInteger(signatureR1, 16);
        BigInteger r2 = new BigInteger(signatureR2, 16);
        BigInteger q  = new BigInteger(DSA_Q, 16);


        BigInteger randomNumber = KeyExtractor.extractRandom(d1, d2, s1, s2, q);
        System.out.println("Random number: " + randomNumber.toString());
        System.out.println("Random number in Hex: " + randomNumber.toString(16));

        BigInteger privateKey = KeyExtractor.extractPrivate(randomNumber, s1, d1, r1, q);
        System.out.println("Private key: " + privateKey.toString());
        System.out.println("Private key in hex: " + privateKey.toString(16));
    }



    /**
     * This methods checks if the number of the arguments in console is correct.
     *
     * @param args
     *            The argument in console.
     * @param argumentsAllowed
     *            the number of arguments that are allowed
     * @exception IllegalArgumentException
     *            if the given argument is not supported.
     */
    public static void checkTheArguments(String[] args, int argumentsAllowed) throws IllegalArgumentException {
        int argumentLength = (args != null) ? args.length : 0;
        if (argumentLength != argumentsAllowed) {
            String out = String.format(ERROR_ARGUMENTS, argumentLength,
                    argumentsAllowed);
            throw new IllegalArgumentException(out);
        }
    }


    /**
     * This method generates a X509Certificate from a file
     */
    public static X509Certificate generateX509Certiticate(String filePath) throws FileNotFoundException, CertificateException {
        FileInputStream is = new FileInputStream(filePath);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate x509certificate  = (X509Certificate) certificateFactory.generateCertificate(is);
        return x509certificate;
    }
}
