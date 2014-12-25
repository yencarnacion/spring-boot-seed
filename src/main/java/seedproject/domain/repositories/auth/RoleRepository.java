package seedproject.domain.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import seedproject.domain.auth.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByAuthority(String Authority);
}