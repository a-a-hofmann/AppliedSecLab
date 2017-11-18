package ch.ethz.asl.ca.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCertificateRepository extends JpaRepository<UserCertificate, String> {

    UserCertificate findBySerialNr(String serialNr);
    
    UserCertificate findBySerialNrAndIssuedTo(String serialNr, User user);

    List<UserCertificate> findAllByIssuedTo(User user);

    List<UserCertificate> findFirstByIssuedToOrderByIssuedOnDesc(User user);

    List<UserCertificate> findFirstByOrderByIssuedOnDesc();

    List<UserCertificate> findAllByIssuedToAndIsRevokedFalse(User user);

    List<UserCertificate> findAllByIsRevokedTrue();
}
