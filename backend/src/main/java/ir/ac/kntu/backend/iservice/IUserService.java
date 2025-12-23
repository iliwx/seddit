package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.*;
import ir.ac.kntu.backend.model.ProfilePhoto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {

    /* Authentication / OTP */
    void requestOTP(UserDTO.LoginRqOTP loginRqOTP);
    UserDTO.LoginRs verifyOTP(UserDTO.OtpVerifyRq suppliedOTP);
    UserDTO.LoginRs login(UserDTO.LoginRq loginRq);
    boolean logout(); // front called on logout

    /* User lifecycle */
    UserDTO.UserCreateRs create(UserDTO.UserCreateRq registerRq);
    UserDTO.UserViewDTO getUserView(Long id, Pageable postsPageable,Pageable communitiesPageable, Pageable commentsPageable); //for a view of other users' profiles
    UserDTO.MinimalView getMinimalView(Long id);
    UserDTO.UserProfileRs profile(Pageable postsPageable, Pageable communitiesPageable, Pageable commentsPageable, Pageable NotificationsPageable); // current authenticated user's full profile
    UserDTO.UserUpdate update(Long id, UserDTO.UserUpdate userUpdate);
    void delete(Long id);

    /* Account status / password */
    UserDTO.UserStatus disableUser(Long userId);
    UserDTO.UserStatus enableUser(Long userId);
    UserDTO.UserCreateRs changePassword(UserDTO.ChangePassRq changePassRq);

    ProfilePhotoDTO uploadProfilePhoto(Long userId, MultipartFile file) throws IOException;
    void deleteProfilePhoto(Long userId);
    ProfilePhoto getProfilePhoto(Long userId);

    SearchDTO.SearchRs<UserDTO.UserViewDTO> search(SearchDTO.SearchRq searchRq);

    /* Preferences Read, Update, Restore to default */
    PreferencesDTO getPreferences();
    PreferencesDTO updatePreferences(PreferencesDTO.PrefUpdateRq preferencesDto);
    PreferencesDTO restoreDefaultPreferences();


    /* Notification signatures (to be implemented later) */
    NotificationDTO sendNotification(Long recipientId, NotificationDTO notificationDto);
    List<NotificationDTO> listNotifications(Pageable pageable); // current user's notifications
    NotificationDTO markNotificationRead(Long notificationId);
    void deleteNotification(Long notificationId);



}
