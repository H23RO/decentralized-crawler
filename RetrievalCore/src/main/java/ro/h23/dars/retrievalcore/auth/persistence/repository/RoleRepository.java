package ro.h23.dars.retrievalcore.auth.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.h23.dars.retrievalcore.auth.persistence.model.RoleType;
import ro.h23.dars.retrievalcore.auth.persistence.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}