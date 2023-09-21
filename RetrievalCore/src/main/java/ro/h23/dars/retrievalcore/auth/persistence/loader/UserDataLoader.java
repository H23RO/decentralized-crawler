package ro.h23.dars.retrievalcore.auth.persistence.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.h23.dars.retrievalcore.auth.persistence.repository.UserRepository;
import ro.h23.dars.retrievalcore.common.exception.PersistenceException;
import ro.h23.dars.retrievalcore.auth.persistence.model.Role;
import ro.h23.dars.retrievalcore.auth.persistence.model.RoleType;
import ro.h23.dars.retrievalcore.auth.persistence.model.User;
import ro.h23.dars.retrievalcore.auth.persistence.repository.RoleRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class UserDataLoader implements ApplicationListener<ContextRefreshedEvent> {


    private static final Logger logger = LogManager.getLogger(UserDataLoader.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        logger.info("Initializing user-role tables (if needed)");

        // create roles
        for (RoleType roleType : RoleType.values()) {
            if (roleRepository.findByName(roleType).isEmpty()) {
                Role role = new Role();
                role.setName(roleType);
                roleRepository.save(role);
            }
        }

        // create users
        if (userRepository.findByUsername("crawler1").isEmpty()) {
            User user = new User("crawler1", "crawler1@dars.h23.ro", passwordEncoder.encode("$#%#$#$$^$Y$Y$Yh56h56h5^H%^JH%j56j^&J67j67J67j34t#$34tG54"));

            Set<Role> rolesSet = new HashSet<>();
            Optional<Role> role = roleRepository.findByName(RoleType.ROLE_CRAWLER);
            if (role.isPresent()) {
                rolesSet.add(role.get());
            } else {
                throw new PersistenceException("Role `ROLE_CRAWLER` is not present in the database");
            }
            user.setRoles(rolesSet);
            userRepository.save(user);
        }

        if (userRepository.findByUsername("scraper1").isEmpty()) {
            User user = new User("scraper1", "scraper1@dars.h23.ro", passwordEncoder.encode("1#%#$#$$^$Y$Y$Yh56h56h5^H%^JH%j56j^&J67j67J67j34t#$34tG57"));

            Set<Role> rolesSet = new HashSet<>();
            Optional<Role> role = roleRepository.findByName(RoleType.ROLE_SCRAPER);
            if (role.isPresent()) {
                rolesSet.add(role.get());
            } else {
                throw new PersistenceException("Role `ROLE_CRAWLER` is not present in the database");
            }
            user.setRoles(rolesSet);
            userRepository.save(user);
        }


    }
}
