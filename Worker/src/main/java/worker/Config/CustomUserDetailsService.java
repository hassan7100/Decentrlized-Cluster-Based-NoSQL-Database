package worker.Config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import worker.User;
import worker.UserRepo;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepo usersRepo;

    public CustomUserDetailsService(UserRepo usersRepo) {
        this.usersRepo = usersRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username)  {
        User user = usersRepo.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(Arrays.asList(user.getRole())));
    }
    private Collection<GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
    }

}
