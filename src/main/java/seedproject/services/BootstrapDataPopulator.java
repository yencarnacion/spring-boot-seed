package seedproject.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seedproject.domain.auth.Role;
import seedproject.domain.auth.User;
import seedproject.domain.repositories.auth.RoleRepository;
import seedproject.domain.repositories.auth.UserRepository;

import javax.transaction.Transactional;

@Service
public class BootstrapDataPopulator implements InitializingBean {

    private final Logger LOG = LoggerFactory.getLogger(BootstrapDataPopulator.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    @Transactional()
    public void afterPropertiesSet() throws Exception {
        LOG.info("Bootstrapping data...");

        createSystemUser();

        LOG.info("...Bootstrapping completed");
    }

    private void createSystemUser() {
        if (userRepository.findOne(1) != null) {
            return;
        }

        LOG.info("... creating system user");

        Role userRole = roleRepository.findByAuthority("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setAuthority("ROLE_USER");
            roleRepository.saveAndFlush(userRole);
        }

        Role adminRole = roleRepository.findByAuthority("ROLE_ADMIN");
        if(adminRole == null){
            adminRole = new Role();
            adminRole.setAuthority("ROLE_ADMIN");
            roleRepository.saveAndFlush(adminRole);
        }

        User adminUser = new User();
        adminUser.setUsername("administrator");
        adminUser.setPassword("password");
        adminUser.setId(1);
        adminUser.setEnabled(true);
        adminUser.getRoles().add(adminRole);
        userRepository.saveAndFlush(adminUser);
    }

}
