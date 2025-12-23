package ir.ac.kntu.backend.DTO;

import ir.ac.kntu.backend.model.ThumbnailStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private UserDTO.MinimalView author;
    private CommunityDTO.MinimalView community;
    private long commentCount;
    private List<AttachmentDTO> attachments; // small meta only
    private long votes;
    private Instant lastModifiedDate;
    private Integer version;


    //requires a separate controller like getPhoto for attachment download...
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AttachmentDTO {

        private Long id;
        private String filename;
        private String contentType;
        private Long size;
        private String url;         // endpoint to stream/download this attachment
        private String thumbnailUrl;       // /api/posts/{postId}/attachments/{id}/thumbnail?v={version}
        private ThumbnailStatus thumbnailStatus;
        private Long thumbnailVersion;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostCreateRequest {

        @NotNull
        private String title;
        private String content;
        @NotNull
        private Long communityId;
        // attachments are provided as MultipartFile[] on the controller side
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostUpdateRequest {
        private String title;
        private String content;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCreateResponse {
        private Long postId;
        private LocalDateTime createdAt;
        private List<AttachmentDTO> attachments;
    }
}
