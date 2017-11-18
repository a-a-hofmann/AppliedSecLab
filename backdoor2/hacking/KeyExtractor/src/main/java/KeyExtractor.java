import java.math.BigInteger;

/**
 * This class is used to as a caculator that extracts the key
 */


public class KeyExtractor {


    /**
     * OpenSSL calculates the DSA signature in the following way:
     * 1. Generate a random key k
     * 2. Calculate r = g^x (mod q or p)
     * 3. Calculate s = inv(k)(H(M) + x*r)
     * 4. Output (r,s)
     *
     * Hence it holds:
     *      k*s - x*r = H(M)
     *  If k is used twice:
     *      k*s1 - x*r = H(M1)
     *      k*s2 - x*r = H(M2)
     *     ---->
     *     k*(s1-s2) = H(M1)-H(M2)
     *      r = (M1 - M2)/(s1 - s2)  (mod q)
     *     @param d1 Digest of the M1, d1 = H(M1) (in decimal)
     *            d2 Digest of the M2, d2 = H(M2) (in decimal)
     *            s1 Second part of the signature (in decimal)
     *            s2 Second part of the signature (in decimal)
     */

    public static BigInteger extractRandom(BigInteger d1, BigInteger d2, BigInteger s1, BigInteger s2, BigInteger q) {
        BigInteger d = d1.subtract(d2);
        BigInteger s = s1.subtract(s2);
        BigInteger sInv = s.modInverse(q);
        BigInteger exp = d.multiply(sInv);
        BigInteger privateKey = exp.mod(q);
        return privateKey;
    }

    /**
     * OpenSSL calculates the DSA signature in the following way:
     * 1. Generate a random key k
     * 2. Calculate r = g^x (mod q or p)
     * 3. Calculate s = inv(k)(H(M) + x*r)
     * 4. Output (r,s)
     *
     * Hence it holds:
     *      k*s - x*r = H(M)
     *     ---->
     *      x = (k*s - H(M))/r
     *     @param k random number
     *            s Second part of the signature (in decimal)
     *            d Digest of the M, d = H(M) (in decimal)
     *            r First part of the signature (in decimal);
     */
    public static BigInteger extractPrivate(BigInteger k, BigInteger s, BigInteger d, BigInteger r, BigInteger q) {
            BigInteger a = k.multiply(s);
            BigInteger b = a.subtract(d);
            BigInteger inv_r = r.modInverse(q);
            BigInteger exp = b.multiply(inv_r);
            BigInteger privateKey = exp.mod(q);
            return privateKey;
    }





}
