package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationsService {

    // creation and search and linking to user pondering...!
    Page<NotificationDTO> getNotifications(Long userId, Pageable pageable);
    long getUnreadNotificationCount(Long userId);
    NotificationDTO markNotificationRead(Long notificationId);
    void markAllNotificationsRead(Long userId);
}
