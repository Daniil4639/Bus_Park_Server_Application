package app.security.user_details;

import app.models.BusParkUser;
import app.repositories.BusParkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusUserService implements UserDetailsService {

    private final BusParkUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<BusParkUser> user = repository.findById(username);

        return user.map(BusUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Incorrect name: " + username));
    }
}