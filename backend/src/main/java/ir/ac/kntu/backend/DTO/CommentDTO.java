package ir.ac.kntu.backend.DTO;

import jakarta.validation.constraints.NotBlank;
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
public abstract class CommentDTO {

    /**
     * Summary DTO used for lists / pages (top-level or flat)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Long id;
        private String text;
        private LocalDateTime createdAt;
        private UserDTO.MinimalView author;
        private Long parentId;     // null if top-level
        private long votes;
        private Instant lastModifiedDate;
        private Integer version;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentCreateRq {
        @NotBlank
        private String text;
        private Long parentId; // optional - reply to this comment
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentUpdateRq {
        @NotBlank
        private String text;
    }

    /**
     * Thread DTO: includes nested replies (small list). Use for top-level comments in user view.
     * Limit the depth and/or number of replies to avoid huge payloads.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentThread extends Summary {
        private List<CommentThread> replies;           // immediate replies (maybe limited)
        private boolean moreReplies = false;  // if true, there are more replies not included
    }
}
