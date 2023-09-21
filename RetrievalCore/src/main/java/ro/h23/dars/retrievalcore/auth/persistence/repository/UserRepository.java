package ro.h23.dars.retrievalcore.auth.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.h23.dars.retrievalcore.auth.persistence.model.Role;
import ro.h23.dars.retrievalcore.auth.persistence.model.RoleType;
import ro.h23.dars.retrievalcore.auth.persistence.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByRolesIn(Collection<Role> roles, Pageable pageable);

    List<User> findByRolesName(RoleType name, Pageable pageable);
}