package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserCertificateServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCertificateRepository userCertificateRepository;

    @Autowired
    private UserCertificateService userCertificateService;

    private User test1;
    private User test2;

    @Before
    public void setUp() throws Exception {
        test1 = new User("test1", "test1");
        test2 = new User("test2", "test2");
        test1 = userRepository.save(test1);
        test2 = userRepository.save(test2);
    }

    @After
    public void tearDown() throws Exception {
        userCertificateRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findBySerialNrAndUser() throws Exception {
        long serialNrCounter = 0;
        UserCertificate cert1 = createCertificate(serialNrCounter++);
        UserCertificate cert2 = createCertificate(serialNrCounter);
        cert1.setIssuedTo(test1);
        cert2.setIssuedTo(test2);

        cert1 = userCertificateRepository.save(cert1);
        cert2 = userCertificateRepository.save(cert2);

        Optional<UserCertificate> certificate = userCertificateService.findBySerialNrAndUser(cert1.getSerialNr(), test1);
        Assert.assertTrue(certificate.isPresent());
        Assert.assertThat(certificate.get(), is(cert1));

        // other cert doesn't exist for user test1
        certificate = userCertificateService.findBySerialNrAndUser(cert2.getSerialNr(), test1);
        Assert.assertFalse(certificate.isPresent());
    }

    @Test
    public void findAllByUser() throws Exception {
        List<UserCertificate> certs = userCertificateService.findAllByUser(test1);
        Assert.assertTrue(certs.isEmpty());

        long serialNrCounter = 0;
        UserCertificate cert1 = createCertificate(serialNrCounter++);
        UserCertificate cert2 = createCertificate(serialNrCounter);
        cert1.revoke();

        cert1.setIssuedTo(test1);
        cert2.setIssuedTo(test1);

        cert1 = userCertificateRepository.save(cert1);
        cert2 = userCertificateRepository.save(cert2);

        certs = userCertificateService.findAllByUser(test1);
        Assert.assertThat(certs.size(), is(2));
        Assert.assertTrue(certs.contains(cert1));
        Assert.assertTrue(certs.contains(cert2));
    }

    @Test
    public void findAllByUserNotRevoked() throws Exception {
        List<UserCertificate> certs = userCertificateService.findAllByUser(test1);
        Assert.assertTrue(certs.isEmpty());

        long serialNrCounter = 0;
        UserCertificate cert1 = createCertificate(serialNrCounter++);
        UserCertificate cert2 = createCertificate(serialNrCounter);
        cert1.revoke();

        cert1.setIssuedTo(test1);
        cert2.setIssuedTo(test1);

        cert1 = userCertificateRepository.save(cert1);
        cert2 = userCertificateRepository.save(cert2);

        certs = userCertificateService.findAllByUserNotRevoked(test1);
        Assert.assertThat(certs.size(), is(1));
        Assert.assertTrue(!certs.contains(cert1));
        Assert.assertTrue(certs.contains(cert2));
    }

    @Test
    public void findLastForUser() throws Exception {
        Optional<UserCertificate> cert = userCertificateService.findLastUserCertificate(test1);
        Assert.assertTrue(Optional.empty().equals(cert));

        long serialNrCounter = 0;
        UserCertificate cert1 = createCertificate(serialNrCounter++);
        Thread.sleep(500); // wait otherwise both certificates will have the same timestamp.
        UserCertificate cert2 = createCertificate(serialNrCounter);
        cert1.revoke();

        cert1.setIssuedTo(test1);
        cert2.setIssuedTo(test1);

        cert1 = userCertificateRepository.save(cert1);
        cert2 = userCertificateRepository.save(cert2);

        cert = userCertificateService.findLastUserCertificate(test1);
        Assert.assertTrue(cert.isPresent());
        Assert.assertThat(cert.get(), is(cert2));
    }

    @Test
    public void findLastCertificate() throws Exception {
        Optional<UserCertificate> cert = userCertificateService.findLastCertificate();
        Assert.assertTrue(Optional.empty().equals(cert));

        long serialNrCounter = 0;
        UserCertificate cert1 = createCertificate(serialNrCounter++);
        Thread.sleep(500); // wait otherwise both certificates will have the same timestamp.
        UserCertificate cert2 = createCertificate(serialNrCounter);
        cert1.revoke();
        Thread.sleep(500);
        UserCertificate cert3 = createCertificate(serialNrCounter);

        cert1.setIssuedTo(test1);
        cert2.setIssuedTo(test2);
        cert3.setIssuedTo(test1);

        cert1 = userCertificateRepository.save(cert1);
        cert2 = userCertificateRepository.save(cert2);
        cert3 = userCertificateRepository.save(cert3);

        cert = userCertificateService.findLastCertificate();
        Assert.assertTrue(cert.isPresent());
        Assert.assertThat(cert.get(), is(cert3));
    }

    @Test
    public void issueCertificateForUser() throws Exception {
        final long serialNr = 12345;
        final String path = "/";
        UserCertificate newCertificate = userCertificateService.issueCertificateForUser(UserSafeProjection.of(test1), serialNr, path);

        Assert.assertNotNull(newCertificate);
        Assert.assertThat(newCertificate.getSerialNr(), is(serialNr));
        Assert.assertThat(newCertificate.getPath(), is(path));
        Assert.assertThat(newCertificate.getIssuedTo(), is(test1));

        Optional<UserCertificate> certFoundInDb = userCertificateService.findBySerialNrAndUser(newCertificate.getSerialNr(), test1);
        Assert.assertTrue(certFoundInDb.isPresent());
        Assert.assertThat(certFoundInDb.get(), is(newCertificate));
    }

    @Test(expected = IllegalArgumentException.class)
    public void issueCertificateNoUser() throws Exception {
        final long serialNr = 12345;
        final String path = "/";
        userCertificateService.issueCertificateForUser(null, serialNr, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void issueCertificateNoPath() throws Exception {
        final long serialNr = 12345;
        userCertificateService.issueCertificateForUser(UserSafeProjection.of(test1), serialNr, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void issueCertificateEmptyPath() throws Exception {
        final long serialNr = 12345;
        userCertificateService.issueCertificateForUser(UserSafeProjection.of(test1), serialNr, "");
    }

    @Test
    public void addCertificateToUser() throws Exception {
        UserCertificate cert = createCertificate(0);

        UserCertificate certificate = userCertificateService.addCertificateToUser(UserSafeProjection.of(test1), cert);

        Assert.assertThat(certificate.getSerialNr(), is(cert.getSerialNr()));
        Assert.assertThat(cert.getIssuedTo(), is(test1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCertificateToUserCertNull() throws Exception {
        userCertificateService.addCertificateToUser(UserSafeProjection.of(test1), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCertificateToUserUserNull() throws Exception {
        userCertificateService.addCertificateToUser(null, createCertificate(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCertificateToUserDoesntExist() throws Exception {
        UserCertificate cert = createCertificate(0);
        userCertificateService.addCertificateToUser(UserSafeProjection.of(new User("test3", "test3")), cert);
    }

    @Test
    public void revokeCertificate() throws Exception {
        UserCertificate cert = createCertificate(0);
        cert.setIssuedTo(test1);

        cert = userCertificateRepository.save(cert);
        Assert.assertFalse(cert.isRevoked());
        Assert.assertNull(cert.getRevokedOn());

        UserCertificate revokedCertificate = userCertificateService.revokeCertificate(test1, cert.getSerialNr());

        Assert.assertThat(cert.getSerialNr(), is(revokedCertificate.getSerialNr()));
        Assert.assertTrue(revokedCertificate.isRevoked());
        Assert.assertNotNull(revokedCertificate.getRevokedOn());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRevokeCertificateForOtherUser() throws Exception {
        UserCertificate cert = createCertificate(0);
        cert.setIssuedTo(test1);

        cert = userCertificateRepository.save(cert);
        Assert.assertFalse(cert.isRevoked());
        Assert.assertNull(cert.getRevokedOn());

        userCertificateService.revokeCertificate(test2, cert.getSerialNr());
    }

    private UserCertificate createCertificate(long id) {
        UserCertificate cert = UserCertificate.issuedNow();
        cert.setSerialNr(id);
        return cert;
    }
}