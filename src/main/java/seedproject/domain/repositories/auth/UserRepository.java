package seedproject.domain.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import seedproject.domain.auth.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
