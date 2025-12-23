package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends IBaseRepository<Notification, Long> {

    Page<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    long countByRecipient_IdAndReadFalse(Long recipientId);

    @EntityGraph(attributePaths = {"images"})
    Page<Notification> findWithImagesByRecipient_IdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // optional:
    List<Notification> findByRecipient_IdAndReadFalse(Long recipientId);
}
