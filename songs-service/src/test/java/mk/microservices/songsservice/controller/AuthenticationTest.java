package mk.microservices.songsservice.controller;

import mk.microservices.songsservice.auth.JwtAuthenticatedUser;
import mk.microservices.songsservice.auth.JwtAuthentication;
import mk.microservices.songsservice.auth.JwtAuthenticationProvider;
import mk.microservices.songsservice.auth.JwtAuthenticationTokenFilter;
import mk.microservices.songsservice.dao.UserDAO;
import mk.microservices.songsservice.model.User;
import mk.microservices.songsservice.services.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import static mk.microservices.songsservice.controller.JwtServiceTest.EXPIRY_IN_SECONDS;
import static mk.microservices.songsservice.controller.JwtServiceTest.SECRET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationTest {

    private final JwtTokenService jwtService = new JwtTokenService(SECRET, EXPIRY_IN_SECONDS);
    private static final User USER = User.builder().userId("hpotter").firstName("Harald").lastName("Töpfer").password("GoldenSnitch1991").build();

    @Mock
    private UserDAO userDAO;
    private String token;

    @BeforeEach
    void setup() {
        token = jwtService.generateToken(USER);
    }

    @Test
    void testBearerTokenIsExtractedFromAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/whoami")
                .header("Authorization", "Bearer " + token)
                .buildRequest(mock(ServletContext.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        JwtAuthenticationTokenFilter filter = new JwtAuthenticationTokenFilter();

        filter.doFilter(request, mock(HttpServletResponse.class), mock(FilterChain.class));

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertFalse(auth.isAuthenticated()); // Not yet authenticated
        assertTrue(auth instanceof JwtAuthentication);
        assertEquals(token, auth.getCredentials());
    }

    @Test
    void testJwtAuthAttemptIsVerified() {
        when(userDAO.getUserById(USER.getUserId())).thenReturn(USER);
        JwtAuthentication tokenAuth = new JwtAuthentication(token);

        Authentication processedAuth = new JwtAuthenticationProvider(jwtService, userDAO).authenticate(tokenAuth);

        assertNotNull(processedAuth);
        assertTrue(processedAuth.isAuthenticated());
        assertTrue(processedAuth instanceof JwtAuthenticatedUser);
        assertEquals(USER.getUserId(), processedAuth.getPrincipal());
    }


}
