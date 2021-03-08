package mk.microservices.songsservice.auth;

import lombok.AllArgsConstructor;
import mk.microservices.songsservice.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String token = (String) authentication.getCredentials();
        final String userId = tokenService.validateTokenAndGetUserId(token);
        if (userId == null) return null;
        return new JwtAuthenticatedUser(userId);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
