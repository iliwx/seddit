package ir.ac.kntu.backend.iservice;

import org.springframework.security.core.context.SecurityContext;


public interface ISecurityService {

    void authenticateByToken(String token);

    void authenticateByToken(String token, SecurityContext context);

    String createToken(Long userId, String role);

    boolean logout();

}
