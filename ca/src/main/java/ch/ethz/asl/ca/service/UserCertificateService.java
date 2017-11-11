package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.model.UserCertificateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCertificateService {

    private final UserCertificateRepository repository;

    public UserCertificateService(UserCertificateRepository repository) {
        this.repository = repository;
    }

    public Optional<UserCertificate> findBySerialNrAndUser(long serialNr, User user) {
        UserCertificate certificate = repository.findBySerialNrAndIssuedTo(serialNr, user);
        return Optional.ofNullable(certificate);
    }

    public List<UserCertificate> findAllByUser(User user) {
        return repository.findAllByIssuedTo(user);
    }

    public List<UserCertificate> findAllByUserNotRevoked(User user) {
        return repository.findAllByIssuedToAndIsRevokedFalse(user);
    }

    public Optional<UserCertificate> findLastUserCertificate(User user) {
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

    public void revokeCertificate(final User user, final String serialNr) {

    }
}
