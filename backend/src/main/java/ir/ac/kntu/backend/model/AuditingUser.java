package ir.ac.kntu.backend.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditingUser implements Serializable {
    private Long userId;
    private String sessionId;
}
