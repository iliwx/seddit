package ir.ac.kntu.backend.iservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface INotificationService {

//    // Called by producers when an event happens
//    NotificationDto createNotification(NotificationCreateRq rq);
//
//    // convenience overload
//    NotificationDto createNotification(Long recipientId, String type, String message);
//
//    // fetch paged notifications for a user
//    Page<NotificationDto> getNotifications(Long userId, Pageable pageable);
//
//    long countUnread(Long userId);
//
//    NotificationDto markAsRead(Long notificationId, Long actingUserId); // returns updated DTO
//
//    void markAllRead(Long userId);
//
//    void deleteNotification(Long notificationId, Long actingUserId);
//
//    // optional: upload image helper
//    void attachImages(Long notificationId, List<MultipartFile> files);
}
