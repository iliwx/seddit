package ir.ac.kntu.backend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class UserDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserUpdate {

        private String name;
        private String family;
        private String email;
        private String description;  // biography
        private LocalDate birthDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserCreateRq {

        @NotNull
        private String username;

        @NotNull
        private String password;

        @NotNull
        private String name;

        @NotNull
        private String family;

        @NotNull
        @Email
        private String email;

        private String description;

        @NotNull
        private LocalDate birthDate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCreateRs {

        private Long id;
        private String username;
        private String password;
        private String name; // first name
        private String family; // last name
        private String email;
        private String description; // biography
        private LocalDate birthDate;
        private boolean enabled;
        private LocalDate createdDate;
        private String token;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserViewDTO {

        private String username;
        private String name;
        private String family;
        private String email;
        private String description;    // biography
        private LocalDate birthDate;
        private LocalDate createdDate;   // joined at

        private ProfilePhotoDTO profilePhoto;
        private List<PostDTO> recentPosts;
        private List<CommunityDTO> recentJoinedCommunities;
        private List<CommentDTO.Summary> recentComments;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserProfileRs {

        private Long id;
        private String username;
        private String name;
        private String family;
        private String email;
        private String description;    // biography
        private LocalDate birthDate;
        private LocalDate createdDate;   // joined at

        private ProfilePhotoDTO profilePhoto;
        private PreferencesDTO preferences;

        private List<PostDTO> posts;
        private List<CommunityDTO> joinedCommunities;
        private List<CommunityDTO> ownedCommunities;
        private List<CommentDTO.Summary> comments;
        private List<NotificationDTO> notifications;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRq {

        private String username; // or phone/email depending on auth
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRqOTP {

        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRs {

        private Long id;
        private String username;
        private String token;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OtpVerifyRq {
        private String otp;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ChangePassRq {
        private String password;
        private String newPassword;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserStatus {
        private boolean enabled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MinimalView {
        private Long id;
        private String username;
        private ProfilePhotoDTO profilePhoto;
    }


}
