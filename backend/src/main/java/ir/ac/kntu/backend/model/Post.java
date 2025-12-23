package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post extends Auditable implements Votable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_title")
    private String title;

    @Column(name = "c_content")
    private String content;

    @Builder.Default
    @Column(name = "n_votes", nullable = false)
    private long votes = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    // comments on this post
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // attachments (images, videos). Cascade so attachments saved/removed with post
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAttachment> attachments = new ArrayList<>();



    //helper methods ensuring Uni-directional object relations
    public void addAttachment(PostAttachment attachment) {
        if (attachment == null) return;
        attachment.setPost(this);
        this.attachments.add(attachment);
    }

    public void removeAttachment(PostAttachment attachment) {
        if (attachment == null) return;
        this.attachments.remove(attachment);
        attachment.setPost(null);
    }
}
