package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post_attachments")
public class PostAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_filename", nullable = false)
    private String filename;

    @Column(name = "c_content_type")
    private String contentType;

    @Column(name = "n_size")
    private Long size;

    @Lob
    @Column(name = "b_data")
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Lob
    @Column(name = "b_thumbnail")
    private byte[] thumbnailData;

    @Column(name = "n_thumbnail_size")
    private Long thumbnailSize;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "c_thumbnail_status", length = 20)
    private ThumbnailStatus thumbnailStatus = ThumbnailStatus.PENDING;

    @Builder.Default
    // increment to bust caches when thumbnail updated
    @Column(name = "n_thumbnail_version", nullable = false)
    private Long thumbnailVersion = 0L;

}
