package ir.ac.kntu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.backend.DTO.CommentDTO;
import ir.ac.kntu.backend.iservice.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment API",
        description = "Endpoints for creating, editing, deleting and listing comments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final ICommentService commentService;



    @Operation(
            summary = "Create a Comment",
            description = "Create a new comment for a post. If parentId is provided in the payload this will be a reply."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDTO.CommentThread.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g. empty text or illegal parent)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post or parent comment not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/posts/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDTO.CommentThread> createComment(@PathVariable Long postId, @Valid @RequestBody CommentDTO.CommentCreateRq rq) {
        return ResponseEntity.ok(commentService.createComment(postId, rq));
    }



    @Operation(summary = "Edit a comment", description = "Edit an existing comment. Only the author (or moderator) may edit.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDTO.Summary.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO.Summary> editComment(@PathVariable Long id, @Valid @RequestBody CommentDTO.CommentUpdateRq rq) {
        return ResponseEntity.ok(commentService.updateComment(id, rq));
    }


    @Operation(summary = "Delete (soft) a comment", description = "Soft-deletes the comment (text redacted). Only author or moderator may delete.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment deleted (soft)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content)
    })
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.deleteComment(id));
    }

    @Operation(summary = "List top-level comments for a post", description = "Return a pageable list of top-level comments for a post. You may optionally include limited replies per top-level comment.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of comment threads",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDTO.CommentThread.class))),
            @ApiResponse(responseCode = "400", description = "Invalid paging or parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
    })
    @GetMapping(value = "/posts/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CommentDTO.CommentThread>> listTopLevelForPost(@PathVariable Long postId, int page, int size,
            @Parameter(description = "maximum immediate replies to attach to each parent (0 = none, Integer.MAX = all)") @RequestParam(defaultValue = "10") int replyLimit,
            @Parameter(description = "Maximum reply depth to fetch (1 = immediate children)") @RequestParam(defaultValue = "5") int maxDepth) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getTopLevelCommentsWithReplies(postId, pageable, replyLimit, maxDepth));
    }


    @Operation(summary = "Get the full thread rooted at a comment", description = "Returns the subtree of replies rooted at the specified comment.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment thread returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDTO.CommentThread.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content)
    })
    @GetMapping(value = "/{id}/thread", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDTO.CommentThread> getCommentThread(
            @Parameter(description = "ID of the comment to fetch thread for", required = true) @PathVariable Long id) {

        return ResponseEntity.ok(commentService.getCommentThread(id));
    }

    @Operation(summary = "List comments authored by a user (paged)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of comment summaries",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDTO.Summary.class))),
            @ApiResponse(responseCode = "400", description = "Invalid paging params", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CommentDTO.Summary>> listUserComments(
            @Parameter(description = "ID of the user whose comments will be listed", required = true) @PathVariable Long userId,
            @Parameter(description = "Page index (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.listUserComments(userId, pageable));
    }

}
