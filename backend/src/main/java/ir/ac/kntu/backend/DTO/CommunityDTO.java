package ir.ac.kntu.backend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CommunityDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate createdAt;
    private long members;
    private CommunityImageDTO avatarImage;
    private CommunityImageDTO bannerImage;


    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommunityCreateRequest {

        @NotNull
        private String name;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommunityUpdateRequest {

        private String name;
        private String description;
    }




    @Getter
    @Setter
    @NoArgsConstructor
    public static class MinimalView {

        private Long id;
        private String name;
        private CommunityImageDTO avatarImage;
    }
}
