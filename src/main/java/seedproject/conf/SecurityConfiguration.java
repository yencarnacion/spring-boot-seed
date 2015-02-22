package seedproject.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import seedproject.domain.auth.Role;
import seedproject.domain.auth.User;
import seedproject.domain.repositories.auth.RoleRepository;
import seedproject.services.Assembler;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Autowired
    private DataSource datasource;
    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private Assembler assembler;
    @Autowired
    RoleRepository roleRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void createRolesIfNotPresent() {
        LOG.info("... creating Roles");

        Role userRole = roleRepository.findByAuthority("ROLE_USER");
        if (userRole == null) {
            userRole = new Role("ROLE_USER");
            roleRepository.saveAndFlush(userRole);
        }

        Role adminRole = roleRepository.findByAuthority("ROLE_ADMIN");
        if(adminRole == null){
            adminRole = new Role("ROLE_ADMIN");
            roleRepository.saveAndFlush(adminRole);
        }

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        //http://stackoverflow.com/questions/20622862/how-to-use-spring-securitys-csrf-feature-for-stateless-endpoints
        //http://stackoverflow.com/questions/21855259/spring-security-rest-authentication-and-csrf?rq=1
         http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionFixation().none().and()
            .csrf().disable();
         */
        http
                .authorizeRequests().anyRequest().authenticated();
        http
                .formLogin().failureUrl("/login?error")
                .defaultSuccessUrl("/")
                .loginPage("/login")
                .permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        createRolesIfNotPresent();
        BCryptPasswordEncoder encoder = passwordEncoder();

        auth.userDetailsService(userDetailsManager).passwordEncoder(encoder);

        if(!userDetailsManager.userExists("administrator")) {

            LOG.info("... creating System Administrator");
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            User userEntity = new User("administrator", encoder.encode("password"), authorities);

            userDetailsManager.createUser(assembler.buildUserFromUserEntity(userEntity));
        }
    }
}
