package Podogonnet.App.entity;

import Podogonnet.App.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(unique = true)
    private String userName;
    private String nombre;
    private String email;
   private String password;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    @OneToMany
    private List<Turno> turno;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol == null) {
            return null;
        } else if (rol.getPermissions()==null) {
            return null;
        }

         List<GrantedAuthority>authorities=  rol.getPermissions().stream().map(
                each->{ String permission= each.name();
                return new SimpleGrantedAuthority(permission);
                }).collect(Collectors.toList());
        authorities.add((new SimpleGrantedAuthority("ROLE_"+this.rol.name())));
        return authorities;


   }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
