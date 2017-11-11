package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.model.UserCertificateRepository;
import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.service.command.CertificateManager;
import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CertificateServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCertificateRepository userCertificateRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateManager certificateManager;

    private User test1;

    HttpServletResponse mockResponse;

    //we have the user "db" whose certificate was issued with serial nr 05.
    @Before
    public void setUp() throws Exception {
        mockResponse = mock(HttpServletResponse.class);
        when(mockResponse.getOutputStream()).thenReturn(new ByteArrayServletOutputStream());

        test1 = new User("db", "D15Licz6");
        test1.setEmail("db@imovie.ch");
        test1.setFirstname("David");
        test1.setLastname("Basin");
        test1 = userRepository.save(test1);

        UserCertificate cert = UserCertificate.issuedNowToUser(05, "etc/ssl/CA/newcerts/db/", test1);
        userCertificateRepository.save(cert);
    }

    @After
    public void tearDown() throws Exception {
        userCertificateRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getCertificate1() throws Exception {
        boolean success = true;
        ServletOutputStream testOutputStream = mockResponse.getOutputStream();
        assertTrue(success == certificateService.getCertificate("05", "db", testOutputStream));
        System.out.println(new String(((ByteArrayServletOutputStream) testOutputStream).toByteArray(), "UTF-8"));

    }

    @Test
    public void getCertificate2() throws Exception {
        boolean success = false;
        ServletOutputStream testOutputStream = mockResponse.getOutputStream();
        assertTrue(success == certificateService.getCertificate("02", "db", testOutputStream));
        //System.out.println(new String(((ByteArrayServletOutputStream) testOutputStream).toByteArray(), "UTF-8"));

    }

    @Test
    public void issueNewCertificate() throws Exception {

    }

    @Test
    public void revokeCertificate() throws Exception {

    }

    private UserCertificate createCertificate(long id) {
        UserCertificate cert = UserCertificate.issuedNow();
        cert.setSerialNr(id);
        return cert;
    }
}