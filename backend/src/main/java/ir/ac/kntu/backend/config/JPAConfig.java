package ir.ac.kntu.backend.config;

import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.model.AuditingUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JPAConfig {
	private static final AuditingUser ANONYMOUS = new AuditingUser(-1L, "anonymous");

	@Bean
	public AuditorAware<AuditingUser> auditorAware() {

		return () -> {
			final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth instanceof SecurityAuthenticationToken) {
				SecurityAuthenticationToken jwt = (SecurityAuthenticationToken) auth;
				return Optional.of(new AuditingUser(jwt.getUserId(), jwt.getSessionId()));
			}
			return Optional.of(ANONYMOUS);
		};
	}
}