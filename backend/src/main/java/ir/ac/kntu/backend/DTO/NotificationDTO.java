package ir.ac.kntu.backend.DTO;

import ir.ac.kntu.backend.model.User;
import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private String type;     // e.g. "COMMENT", "MENTION"
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private User recipient;
}

