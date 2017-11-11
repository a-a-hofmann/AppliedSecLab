package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.model.UserCertificateRepository;
import ch.ethz.asl.ca.model.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

    public Optional<UserCertificate> findBySerialNrAndUser(long serialNr, User user) {
        Assert.notNull(user, "User cannot be null.");

        UserCertificate certificate = repository.findBySerialNrAndIssuedTo(serialNr, user);
        return Optional.ofNullable(certificate);
    }

    public List<UserCertificate> findAllByUser(User user) {
        Assert.notNull(user, "User cannot be null.");

        return repository.findAllByIssuedTo(user);
    }

    public List<UserCertificate> findAllByUserNotRevoked(User user) {
        Assert.notNull(user, "User cannot be null.");

        return repository.findAllByIssuedToAndIsRevokedFalse(user);
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

    public UserCertificate addCertificateToUser(User user, UserCertificate certificate) {
        Assert.notNull(certificate, "Certificate cannot be null.");
        Assert.notNull(user, "User cannot be null.");
        Assert.isTrue(userRepository.exists(user.getUsername()), String.format("User [%s] doesn't exist.", user.getUsername()));

        certificate.setIssuedTo(user);
        return certificate;
    }

    public UserCertificate revokeCertificate(User user, long serialNr) {
        Assert.notNull(user, "User cannot be null.");

        UserCertificate certificate = repository.findBySerialNrAndIssuedTo(serialNr, user);
        Assert.notNull(certificate, String.format("No certificate found for user [%s] and serialNr [%d]", user.getUsername(), serialNr));
        
        certificate.revoke();
        return certificate;
    }
}
