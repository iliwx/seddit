package ir.ac.kntu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.backend.DTO.PostDTO;
import ir.ac.kntu.backend.iservice.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Tag(
        name = "Post API",
        description = "Endpoints for creating posts, listing posts and downloading attachments / thumbnails"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final IPostService postService;



    @Operation(
            summary = "Create a Post (multipart)",
            description = "Create a new post. This endpoint accepts multipart/form-data where the JSON payload " +
                    "matches PostDTO.CreateRequest and attachments are binary file parts (0..N). " +
                    "Example parts: authorId (form field), payload (JSON part), attachments (file parts)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PostDTO.PostCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or file too large", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping(value = "/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDTO.PostCreateResponse> createPost(@Valid @RequestBody PostDTO.PostCreateRequest request, @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) throws Exception {

        return ResponseEntity.ok(postService.createPost(request,attachments));
    }


    @Operation(summary = "List posts (paged)", description = "Return a pageable list of posts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of posts",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PostDTO.class)))
    })
    @GetMapping("/")
    public ResponseEntity<Page<PostDTO>> listPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.listPosts(PageRequest.of(page, size)));
    }


    @Operation(summary = "List posts by community (paged)", description = "Return posts belonging to a community.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of posts for given community",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PostDTO.class)))
    })
    @GetMapping("/community/{communityId}")
    public ResponseEntity<Page<PostDTO>> listByCommunity(@PathVariable Long communityId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.listPostsByCommunity(communityId, PageRequest.of(page, size)));
    }


    @Operation(summary = "List posts by user (paged)", description = "Return posts authored by a specific user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of posts for given user",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PostDTO.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostDTO>> listByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.listUserPosts(userId, PageRequest.of(page, size)));
    }



    @Operation(summary = "Get post", description = "Get a single post by id (full detail).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post detail",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }


    @Operation(summary = "Update post (text fields only)",
            description = "Update textual fields of a post. Attachments cannot be updated through this endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated post returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostDTO.PostUpdateRequest rq) {
        return ResponseEntity.ok(postService.updatePost(id, rq));
    }


    @Operation(summary = "Delete post")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(summary = "Download an attachment (binary stream)",
            description = "Stream the binary content of an attachment. Sets Content-Disposition to trigger download.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attachment stream",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{postId}/attachments/{attId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long postId, @PathVariable Long attId) {

        Resource res = postService.streamAttachment(postId, attId);
        PostDTO.AttachmentDTO meta = postService.getAttachmentMeta(postId, attId);
        MediaType mt = MediaType.APPLICATION_OCTET_STREAM;
        try { if (meta.getContentType() != null) mt = MediaType.parseMediaType(meta.getContentType()); } catch (Exception ignored) {}
        return ResponseEntity.ok()
                .contentType(mt)
                .contentLength(meta.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFilename() + "\"")
                .body(res);
    }


    @Operation(summary = "Get attachment thumbnail (image)",
            description = "Return thumbnail bytes for an attachment. If the thumbnail is not yet ready, a small placeholder image is returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image stream",
                    content = {
                            @Content(mediaType = "image/jpeg", schema = @Schema(type = "string", format = "binary")),
                            @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{postId}/attachments/{attId}/thumbnail")
    public ResponseEntity<Resource> downloadAttachmentThumbnail(@PathVariable Long postId, @PathVariable Long attId) {

        Resource res = postService.streamAttachmentThumbnail(postId, attId);
        PostDTO.AttachmentDTO meta = postService.getAttachmentMeta(postId, attId);
        MediaType mt;
        if (meta.getThumbnailStatus() != null && meta.getThumbnailStatus().name().equals("DONE")) {
            mt = MediaType.IMAGE_JPEG;
        } else {
            mt = MediaType.IMAGE_PNG;
        }
        long maxAge = (meta.getThumbnailStatus() != null && meta.getThumbnailStatus().name().equals("DONE")) ? 60L * 60L * 24L * 7L : 5L;
        return ResponseEntity.ok()
                .contentType(mt)
                .cacheControl(CacheControl.maxAge(maxAge, TimeUnit.SECONDS).cachePublic())
                .body(res);
    }


    //TODO: create endpoint for thumbnail generation with custom user-defined frame as thumbnail



}
