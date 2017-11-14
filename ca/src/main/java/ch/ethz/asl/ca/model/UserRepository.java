package ch.ethz.asl.ca.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    UserSafeProjection findByUsername(String username);

    UserSafeProjection findByEmail(String email);
}
