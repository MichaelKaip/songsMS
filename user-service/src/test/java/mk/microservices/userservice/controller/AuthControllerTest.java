package mk.microservices.userservice.controller;

import mk.microservices.userservice.auth.JwtAuthenticatedUser;
import mk.microservices.userservice.dao.UserDao;
import mk.microservices.userservice.model.User;
import mk.microservices.userservice.services.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private UserDao userDao;
    private MockMvc mockMvc;
    private User user_valid;
    private User user_invalid;
    private JwtTokenService tokenService;

    @BeforeEach
    void setup() {
        userDao = mock(UserDao.class);
        tokenService = mock(JwtTokenService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(userDao, tokenService)).build();

        user_valid = User.builder()
                .userId("eschuler")
                .password("pass1234")
                .firstName("Elena")
                .lastName("Schuler")
                .build();

        user_invalid = User.builder()
                .userId("gibtsnicht")
                .firstName("Harry")
                .lastName("Potter")
                .password("guckstdu")
                .build();
    }


    @Test
    void authorize_success() {
        when(userDao.getUserById(any(String.class))).thenReturn(user_valid);
        try {
            String accessorJson = "{\"userId\":\"eschuler\",\"password\":\"pass1234\"}";
            mockMvc.perform(post("/auth").content(accessorJson).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN));
            verify(tokenService).generateToken(any(User.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void authorize_No_matching_userId_pw_combi_should_return_401() {
        when(userDao.getUserById(any(String.class))).thenReturn(user_invalid);
        try {
            String accessorJson = "{\"userId\":\"gibtsnicht\",\"password\":\"passtnicht\"}";
            mockMvc.perform(post("/auth").content(accessorJson).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
            verifyZeroInteractions(tokenService);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void dataformat_not_json_should_return_400() {
        when(userDao.getUserById(any(String.class))).thenReturn(user_invalid);
        try {
            String accessorJson = "<\"userId\":\"gibtsnicht\",\"password\":\"passtnicht\">";
            mockMvc.perform(post("/auth").content(accessorJson).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
            verifyZeroInteractions(tokenService);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void whois_endpoint_returns_id_of_authenticated_user() throws Exception {
        JwtAuthenticatedUser auth = new JwtAuthenticatedUser(user_valid.getUserId());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/whoami"))
                .andExpect(status().isOk())
                .andExpect(content().string(user_valid.getUserId()));
    }

}