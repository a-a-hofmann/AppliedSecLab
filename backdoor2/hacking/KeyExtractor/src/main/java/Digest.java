import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.crypto.digests.SHA1Digest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * This class calculates the SHA1 of a given certificate, which is used for verification or the signature.
 */
public class Digest {


    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final X509Certificate x509certificate;

    public Digest(X509Certificate x509Certificate) {
        this.x509certificate = x509Certificate;
    }

    public String getDigest() throws IOException, CertificateException {


        //FileInputStream is = new FileInputStream(filePath);
        //CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        //X509Certificate x509certificate  = (X509Certificate) certificateFactory.generateCertificate(is);

        ASN1InputStream asn1inputstream = new ASN1InputStream(new ByteArrayInputStream(x509certificate.getTBSCertificate()));
        TBSCertificateStructure tbsCert = TBSCertificateStructure.getInstance(asn1inputstream.readObject());

        SHA1Digest digester = new SHA1Digest();
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream dOut = new DEROutputStream(bOut);
        dOut.writeObject(tbsCert);
        byte[] certBlock = bOut.toByteArray();

        // first create digest
        digester.update(certBlock, 0, certBlock.length);
        byte[] hash = new byte[digester.getDigestSize()];
        digester.doFinal(hash, 0);
        System.out.println("SIGNATURE OF THE FIRST FILE IS: " + bytesToHex(x509certificate.getSignature()));


        return bytesToHex(hash);
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String getS() {
        byte[] signature = x509certificate.getSignature();
        String rs = bytesToHex(signature);
        //the first 8 bytes are something else and in the middle - weird (since it is not specified in the standard)!
        String s = rs.substring(11+(rs.length() - 8)/2);
        return s;
    }

    public String getR() {
        byte[] signature = x509certificate.getSignature();
        String rs = bytesToHex(signature);
        //the first 8 bytes are something else and in the middle - weird (since it is not specified in the standard)!
        String r = rs.substring(8, 8+(rs.length() - 8)/2 - 1);
        return r;
    }
}
