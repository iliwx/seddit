package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
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
@Table(name = "communities")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_name", unique = true, nullable = false)
    private String name;

    @Column(name = "c_description", length = 2048)
    private String description;

    @CreationTimestamp
    @Column(name = "d_created", nullable = false, updatable = false)
    private LocalDate createdAt;

    // Owner of the community
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // Members — inverse side of user's joinedCommunities
    @ManyToMany(mappedBy = "joinedCommunities")
    private Set<User> members = new HashSet<>();

    // Posts inside community
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    // avatar/profile image (square)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_image_id")
    private CommunityImage avatarImage;

    // banner/wide image (top of community page)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_image_id")
    private CommunityImage bannerImage;
}
