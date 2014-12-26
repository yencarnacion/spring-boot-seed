package seedproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seedproject.domain.auth.Role;
import seedproject.domain.auth.User;
import seedproject.domain.repositories.auth.RoleRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service("assembler")
public class Assembler {

    @Autowired
    RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public org.springframework.security.core.userdetails.User buildUserFromUserEntity(User userEntity) {

        String username = userEntity.getUsername();
        String password = userEntity.getPassword();
        boolean enabled = userEntity.getEnabled();
        boolean accountNonExpired = userEntity.getAccountExpired();
        boolean credentialsNonExpired = userEntity.getPasswordExpired();
        boolean accountNonLocked = userEntity.getAccountLocked();
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Role role : userEntity.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(username, password, enabled,
                accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return user;
    }

    @Transactional(readOnly = true)
    public User buildUserEntityFromUserDetails(UserDetails userDetails){
        User userEntity = new User();
        userEntity.setUsername(userDetails.getUsername());
        userEntity.setPassword(userDetails.getPassword());
        userEntity.setEnabled(userDetails.isEnabled());
        userEntity.setAccountExpired(userDetails.isAccountNonExpired());
        userEntity.setPasswordExpired(userDetails.isCredentialsNonExpired());
        userEntity.setAccountLocked(userDetails.isAccountNonLocked());
        Set<Role> roles = new HashSet<Role>();
        for(GrantedAuthority authority: userDetails.getAuthorities()){
            roles.add( roleRepository.findByAuthority(authority.getAuthority()));
        }
        userEntity.setRoles(roles);
        return userEntity;
    }
}
