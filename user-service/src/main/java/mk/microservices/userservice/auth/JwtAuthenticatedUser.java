package mk.microservices.userservice.auth;

import lombok.AllArgsConstructor;
import mk.microservices.userservice.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@AllArgsConstructor
public class JwtAuthenticatedUser implements Authentication {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public User getDetails() {
        return user;
    }

    @Override
    public String getPrincipal() {
        return user.getUserId();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return "Jwt Authenticated User";
    }
}
