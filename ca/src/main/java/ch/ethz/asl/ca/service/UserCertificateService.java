package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.model.UserCertificateRepository;
import ch.ethz.asl.ca.model.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserCertificateService {

    private final UserCertificateRepository repository;

    private final UserRepository userRepository;

    public UserCertificateService(UserCertificateRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public boolean isCertificateRevoked(final String serialNr) {
        UserCertificate certificate = repository.findBySerialNrIgnoreCase(serialNr);
        return certificate == null || certificate.isRevoked();
    }

    public Optional<UserCertificate> findBySerialNrAndUser(String serialNr, User user) {
        Assert.notNull(user, "User cannot be null.");

        UserCertificate certificate = repository.findBySerialNrAndIssuedTo(serialNr, user);
        return Optional.ofNullable(certificate);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserCertificate> findAll() {
        return repository.findAll();
    }

    public List<UserCertificate> findAllByUser(User user) {
        Assert.notNull(user, "User cannot be null.");

        return repository.findAllByIssuedTo(user);
    }

    public List<UserCertificate> findAllByUserNotRevoked(User user) {
        Assert.notNull(user, "User cannot be null.");

        return repository.findAllByIssuedToAndIsRevokedFalse(user);
    }

    public List<UserCertificate> findAllRevoked() {
        return repository.findAllByIsRevokedTrue();
    }

    public Optional<UserCertificate> findLastUserCertificate(User user) {
        Assert.notNull(user, "User cannot be null.");

        List<UserCertificate> lastCert = repository.findFirstByIssuedToOrderByIssuedOnDesc(user);
        if (lastCert.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lastCert.get(0));
    }

    public Optional<UserCertificate> findLastCertificate() {
        List<UserCertificate> lastCert = repository.findFirstByOrderByIssuedOnDesc();
        if (lastCert.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lastCert.get(0));
    }

    public UserCertificate issueCertificateForUser(User user, final String serialNr, final String path) {
        Assert.notNull(user, "User cannot be null.");
        Assert.isTrue(!repository.exists(serialNr), String.format("Certificate already exists in the db for serialNr [%s]", serialNr));
        Assert.isTrue(!StringUtils.isEmpty(path), "No path to certificate given.");

        UserCertificate certificate = UserCertificate.issuedNowToUser(serialNr, path, user);
        return repository.save(certificate);
    }

    public UserCertificate addCertificateToUser(User user, UserCertificate certificate) {
        Assert.notNull(certificate, "Certificate cannot be null.");
        Assert.notNull(user, "User cannot be null.");
        Assert.isTrue(userRepository.exists(user.getUsername()), String.format("User [%s] doesn't exist.", user.getUsername()));

        certificate.setIssuedTo(user);
        return certificate;
    }

    public UserCertificate revokeCertificate(User user, String serialNr) {
        Assert.notNull(user, "User cannot be null.");

        UserCertificate certificate;
        if (user.getUsername().equals("admin")) {
            certificate = repository.findBySerialNrIgnoreCase(serialNr);
        } else {
            certificate = repository.findBySerialNrAndIssuedTo(serialNr, user);
        }
        Assert.notNull(certificate, String.format("No certificate found for user [%s] and serialNr [%s]", user.getUsername(), serialNr));

        certificate.revoke();
        return repository.save(certificate);
    }

    public long countNumberOfCertificates() {
        return repository.count();
    }
}
