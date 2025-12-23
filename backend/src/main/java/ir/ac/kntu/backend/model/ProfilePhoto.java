package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "profile_photos")
public class ProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_filename")
    private String filename;

    @Column(name = "n_size")
    private Long size;

    @Lob
    @Column(name = "b_data")
    private byte[] data;

    @CreationTimestamp
    @Column(name = "d_uploaded", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
}
