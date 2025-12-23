package ir.ac.kntu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(name = "d_created_date", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "f_created_by_user_id", updatable = false, nullable = false)),
            @AttributeOverride(name = "sessionId", column = @Column(name = "c_created_at_session_id", updatable = false, nullable = false))
    })
    @Embedded
    private AuditingUser createdBy;

    @LastModifiedDate
    @Column(name = "d_last_modified_date")
    private Instant lastModifiedDate;

    @LastModifiedBy
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "f_last_modified_by_user_id")),
            @AttributeOverride(name = "sessionId", column = @Column(name = "c_last_modified_at_sessionId"))
    })
    @Embedded
    private AuditingUser lastModifiedBy;

    @Version
    @Column(name = "n_version", nullable = false)
    private Integer version;
}
