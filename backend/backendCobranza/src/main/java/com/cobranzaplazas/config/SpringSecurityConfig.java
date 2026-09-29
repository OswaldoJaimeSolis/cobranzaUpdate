package com.cobranzaplazas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Component-based security configuration (Spring Security 6 style).
 *
 * NOTE: this class intentionally reproduces the legacy behaviour of the
 * previous {@code WebSecurityConfigurerAdapter} implementation as-is:
 * a single hardcoded in-memory user, plaintext password comparison via
 * {@link NoOpPasswordEncoder}, CSRF disabled and {@code permitAll()} on every
 * path (which effectively makes the whole API anonymous). That is a known
 * weakness and should be revisited, but hardening it is deliberately out of
 * scope of the Java 21 / Spring Boot 3 upgrade.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SpringSecurityConfig {

	/**
	 * Create a user
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername("cobranza").password("12345").roles("ADMIN").build();
		return new InMemoryUserDetailsManager(user);
	}

	// Secure the endpoins with HTTP Basic authentication
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.httpBasic(httpBasic -> httpBasic.realmName("cobranza"))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(requests -> requests.requestMatchers("/**").permitAll().anyRequest().authenticated());

		return http.build();
	}

	@Bean
	public NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}

}
