package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_username", unique = true, nullable = false)
    @NotNull
    private String username;

    @Column(name = "c_password", nullable = false)
    @NotNull
    private String password;

    @Column(name = "c_first_name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "c_last_name", nullable = false)
    @NotNull
    private String family;

    @Email
    @Column(name = "c_email", unique = true)
    private String email;

    @Column(name = "c_description", length = 2048)
    private String description; // biography

    @Column(name = "d_birth_date")
    private LocalDate birthDate;

    @Builder.Default
    @Column(name = "b_enabled", columnDefinition = "boolean default true")
    private boolean enabled = true;

    // When account was created / joined at
    @CreationTimestamp
    @Column(name = "d_created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    // Profile photo (one-to-one). stored as a separate entity for flexibility.
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_photo_id")
    private ProfilePhoto profilePhoto;

    // Preferences (one-to-one)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "preferences_id")
    private Preferences preferences = new Preferences();

    // Communities this user joined (many-to-many)
    @ManyToMany
    @JoinTable(
            name = "t_user_joined_communities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id")
    )
    private Set<Community> joinedCommunities = new HashSet<>();

    // Communities owned by this user (one-to-many)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Community> ownedCommunities = new HashSet<>();

    // Posts authored by this user
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    // Comments authored by this user
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    // Notifications received by user
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notification> notifications = new HashSet<>();

}
