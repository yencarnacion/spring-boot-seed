package seedproject.domain.auth;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer id;

    @Size(min=1)
    @Column(nullable=false, length=256, unique=true)
    String username;

    @Size(min=1)
    @Column(nullable=false, length=256)
    String password;

    @Column(nullable=false)
    Boolean enabled = true;

    @Column(nullable=false)
    Boolean accountExpired = false;

    @Column(nullable=false)
    Boolean accountLocked = false;

    @Column(nullable=false)
    Boolean passwordExpired = false;

    @ManyToMany
    @JoinTable(name = "USER_ROLE",
            joinColumns =  @JoinColumn(name = "USER_ID", referencedColumnName="ID") ,
            inverseJoinColumns =  @JoinColumn(name = "ROLE_ID", referencedColumnName="ID") )


    private Set<Role> roles = new HashSet<Role>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAccountExpired() {
        return accountExpired;
    }

    public void setAccountExpired(Boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Boolean getPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(Boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public User() {}
    public User (String username, String password, List<GrantedAuthority> authorities){
        this.username = username;
        this.password = password;
        for(GrantedAuthority authority: authorities){
            roles.add(new Role(authority.getAuthority()));
        }
    }
}