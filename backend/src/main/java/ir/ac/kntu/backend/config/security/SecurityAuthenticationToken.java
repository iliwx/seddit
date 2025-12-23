package ir.ac.kntu.backend.config.security;


import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class SecurityAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private final Long userId;
    private final String sessionId;

    public SecurityAuthenticationToken(String token, Long userId, String sessionId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.userId = userId;
        this.sessionId = sessionId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        throw new RuntimeException("JwtAuthenticationToken: No Credentials");
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}