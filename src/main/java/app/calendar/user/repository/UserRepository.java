package app.calendar.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import app.calendar.user.domain.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}