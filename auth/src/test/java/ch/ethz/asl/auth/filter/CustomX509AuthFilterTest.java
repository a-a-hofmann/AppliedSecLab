package ch.ethz.asl.auth.filter;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.core.Is.is;

public class CustomX509AuthFilterTest {

    public static String padLeft(long s) {
        return String.format("%02x", s);
    }

    @Test
    public void name() throws Exception {
        BigInteger bigInteger = BigInteger.valueOf(1);
        String s;

        s = padLeft(bigInteger.longValue());

        Assert.assertThat(s, is("01"));

        bigInteger = BigInteger.valueOf(10);
        s = padLeft(bigInteger.longValue());

        Assert.assertThat(s, is("0a"));

        bigInteger = BigInteger.valueOf(11);
        s = padLeft(bigInteger.longValue());

        Assert.assertThat(s, is("0b"));

        bigInteger = BigInteger.valueOf(100);
        s = padLeft(bigInteger.longValue());

        Assert.assertThat(s, is("64"));


    }
}