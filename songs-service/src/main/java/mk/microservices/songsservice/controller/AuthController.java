package mk.microservices.songsservice.controller;

import lombok.AllArgsConstructor;
import mk.microservices.songsservice.dao.UserDAO;
import mk.microservices.songsservice.model.Accessor;
import mk.microservices.songsservice.model.User;
import mk.microservices.songsservice.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor(onConstructor_ = @Autowired)
@RestController
public class AuthController {

    private final UserDAO userDao;
    private final JwtTokenService tokenService;

    @PostMapping(value = "/auth", headers = "Accept=application/json", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> authorize(@RequestBody Accessor accessor) {
        // Get User
        final User user = userDao.getUserById(accessor.getUserId());

        // Verify Login Details
        if (user != null && user.getPassword().equals(accessor.getPassword()) &&
                user.getUserId().equals(accessor.getUserId())) {

            final String token = tokenService.generateToken(user);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.TEXT_PLAIN);
            return ResponseEntity.ok().headers(header).body(token);

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/whoami", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> whoami() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(auth.getPrincipal().toString());
    }
}
