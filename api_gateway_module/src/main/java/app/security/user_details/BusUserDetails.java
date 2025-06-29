package app.security.user_details;

import app.models.BusParkUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class BusUserDetails implements UserDetails {

    private final BusParkUser busParkUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return busParkUser.getRoles().stream()
                .map(elem -> new SimpleGrantedAuthority("ROLE_" + elem))
                .toList();
    }

    @Override
    public String getPassword() {
        return busParkUser.getPassword();
    }

    @Override
    public String getUsername() {
        return busParkUser.getUsername();
    }
}