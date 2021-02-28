package mk.microservices.userservice.auth;

import lombok.AllArgsConstructor;
import mk.microservices.userservice.dao.UserDao;
import mk.microservices.userservice.model.User;
import mk.microservices.userservice.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenService tokenService;
    private final UserDao userDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String token = (String) authentication.getCredentials();
        final String userId = tokenService.validateTokenAndGetUserId(token);
        if (userId == null) return null;
        final User user = userDao.getUserById(userId);
        if (user == null) return null;
        return new JwtAuthenticatedUser(user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
