package ir.ac.kntu.backend.service;

import io.jsonwebtoken.*;
import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.iservice.ISecurityService;
import ir.ac.kntu.backend.model.RedisToken;
import ir.ac.kntu.backend.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService implements ISecurityService {


    private final RedisTokenRepository redisTokenRepository;
    private static final String USER_ID_CLAIM = "uid";
    private static final String ROLE_CLAIM = "role";
    private final SecretKey secretKey;


    // ------------------------------

    @Override
    public void authenticateByToken(String token) {
        authenticateByToken(token, SecurityContextHolder.getContext());
    }

    @Override
    public void authenticateByToken(String redisId, SecurityContext context) {

        Optional<RedisToken> redisTokenOptional = redisTokenRepository.findById(redisId);
        RedisToken redisToken;
        if(redisTokenOptional.isPresent())
            redisToken = redisTokenOptional.get();
        else return;


        final Claims claims;
        try {

            JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build();

            Jws<Claims> jws = parser.parseClaimsJws(redisToken.getTokenStr());
            claims = jws.getBody();

        } catch (Exception e) {
            log.warn("SecurityService.authenticateByToken - JWT Parse: ({}) {}",
                    e.getClass().getSimpleName(), e.getMessage());
            return;
        }

        final Long userId = assertValue(claims.get(USER_ID_CLAIM, Long.class), "Invalid Token: No UID");
        final List<String> roleList = assertValue(claims.get(ROLE_CLAIM, List.class), "Invalid Token: No Role");

        log.debug("AuthenticateByToken: userId=[{}] role=[{}]", userId, roleList);

        final SecurityAuthenticationToken authToken = new SecurityAuthenticationToken(
                redisToken.getTokenStr(),
                userId,
                redisToken.getId(),
                roleList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        context.setAuthentication(authToken);

        log.debug("Authenticated userId={} authorities={}",
                authToken.getUserId(), authToken.getAuthorities());
    }

    @Override
    public String createToken(Long userId, String role) {

        final Map<String, Object> claims = new HashMap<>();
        String sessionId = UUID.randomUUID().toString();
        claims.put(USER_ID_CLAIM, userId);
        claims.put(ROLE_CLAIM, Collections.singletonList(role));

        final Date expiration = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        final String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();

        final SecurityAuthenticationToken authToken = new SecurityAuthenticationToken(
                jwtToken,
                userId,
                sessionId,
                Collections.singletonList(new SimpleGrantedAuthority(role)));

        SecurityContextHolder.getContext().setAuthentication(authToken);

        RedisToken redisToken = new RedisToken();
        redisToken.setId(sessionId);
        redisToken.setTokenStr(jwtToken);
        redisTokenRepository.save(redisToken);

        return redisToken.getId();
    }

    @Override
    public boolean logout() {

        try {
            redisTokenRepository.deleteById(((SecurityAuthenticationToken)SecurityContextHolder.getContext().getAuthentication()).getSessionId());
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    // ------------------------------

    private <T> T assertValue(T val, String message) {
        if (val == null) {
            throw new BadCredentialsException(message);
        }
        return val;
    }

}
