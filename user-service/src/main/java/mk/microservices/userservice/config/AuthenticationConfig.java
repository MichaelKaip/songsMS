package mk.microservices.userservice.config;

import lombok.AllArgsConstructor;
import mk.microservices.userservice.auth.JwtAuthenticationEntryPoint;
import mk.microservices.userservice.auth.JwtAuthenticationProvider;
import mk.microservices.userservice.auth.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor(onConstructor_ = @Autowired)
@PropertySource("classpath:jwt.properties")
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationProvider authProvider;

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) {
        builder.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // No need for csrf support
                .csrf().disable()
                // Make sure we return 401 on unauthorized requests
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // No sessions
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // Authorization...
                .authorizeRequests()
                // Is not required for the auth and version endpoints
                .antMatchers("/rest/auth", "/rest/version").permitAll()
                // Is required for any other endpoint
                .anyRequest().authenticated();
        // Add the filter
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        // Prevent caching of our responses
        http.headers().cacheControl();
    }
}
