package seedproject.domain.auth;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Size(min = 1)
    @Column(nullable = false, length = 256, unique = true)
    String authority;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<User>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


    public Role() {}
    public Role(String authority){
        setAuthority(authority);
    }
}