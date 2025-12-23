package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "COMMENT", "MENTION", "MODERATION"
    @Column(name = "c_type")
    private String type;

    @Column(name = "c_message", length = 2048)
    private String message;

    @CreationTimestamp
    @Column(name = "d_created", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "b_read", columnDefinition = "boolean default false")
    private boolean read = false;

    // recipient of the notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
}
