package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "preferences")
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    // example preference fields - extend as required
    @Column(name = "b_email_notifications", columnDefinition = "boolean default true")
    private boolean emailNotifications = true;

    @Builder.Default
    @Column(name = "c_theme", length = 50)
    private String theme = "system"; // e.g., light/dark/system

    @Builder.Default
    @Column(name = "c_language", length = 10)
    private String language = "en";

    @Builder.Default
    @Column(name = "b_show_nsfw", columnDefinition = "boolean default false")
    private boolean showNsfw = false;
}
