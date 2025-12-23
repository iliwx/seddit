package ir.ac.kntu.backend.config.security;


import ir.ac.kntu.backend.Properties;
import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.Properties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static ir.ac.kntu.backend.Properties.ETokenMedia.cookie;
import static ir.ac.kntu.backend.Properties.ETokenMedia.header;

@RequiredArgsConstructor
@Component
public class SecurityHandler {

    private final Properties properties;

    public String readToken(HttpServletRequest request) {

        final String tokenKey = properties.getSecurity().getTokenKey();

        return switch (properties.getSecurity().getTokenMedia()) {
            case header -> request.getHeader(tokenKey);
            case cookie -> Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                    .filter(c -> tokenKey.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        };
    }

    public void setTokenAsCookie(HttpServletResponse response, SecurityContext context) {
        final String tokenKey = properties.getSecurity().getTokenKey();

        final Authentication authentication = context.getAuthentication();
        if (authentication instanceof SecurityAuthenticationToken sat) {
            String sendTokenValue = sat.getToken();
            final Cookie cookie = new Cookie(tokenKey, sendTokenValue);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
        } else {
            final Cookie cookie = new Cookie(tokenKey, "-");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

    }
}
