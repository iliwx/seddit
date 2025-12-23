package ir.ac.kntu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.backend.DTO.CommunityDTO;
import ir.ac.kntu.backend.DTO.CommunityImageDTO;
import ir.ac.kntu.backend.iservice.ICommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Community API", description = "Community CRUD, membership and image (avatar / banner) endpoints")
@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final ICommunityService communityService;

    @Operation(summary = "List all communities (paged)",
            description = "Return a pageable list of communities. Response is a Page containing CommunityDTO objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of communities",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommunityDTO.class)))
    })
    @GetMapping("/")
    public ResponseEntity<Page<CommunityDTO>> listAllCommunities(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(communityService.listAllCommunities(pageable));
    }

    @Operation(summary = "List communities the user joined (paged)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of joined communities",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommunityDTO.class)))
    })
    @GetMapping("/joined/{userId}")
    public ResponseEntity<Page<CommunityDTO>> listJoined(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        return ResponseEntity.ok(communityService.listJoinedCommunities(userId, p));
    }


    @Operation(summary = "List communities the user joined (paged)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of joined communities",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommunityDTO.class)))
    })
    @GetMapping("/owned/{userId}")
    @PreAuthorize("#userId == authentication.principal")
    public ResponseEntity<Page<CommunityDTO>> listOwned(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        return ResponseEntity.ok(communityService.listOwnedCommunities(userId, p));
    }


    @Operation(summary = "Create a community",
            description = "Create a new community. The request body should contain name and description.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Community created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityDTO> createCommunity(@Valid @RequestBody CommunityDTO.CommunityCreateRequest rq) {
        return ResponseEntity.ok(communityService.createCommunity(rq));
    }

    @Operation(summary = "Get a community by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Community found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunity(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getCommunity(id));
    }

    @Operation(summary = "Update a community",
            description = "Update community metadata (name, description). Only authenticated users (owner check performed by service).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Community updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityDTO> updateCommunity(@PathVariable Long id,@Valid @RequestBody CommunityDTO.CommunityUpdateRequest rq) {
        return ResponseEntity.ok(communityService.updateCommunity(id, rq));
    }


    @Operation(summary = "Delete a community")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }

    // membership

    @Operation(summary = "Join a community",
            description = "Authenticated user joins the specified community.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Joined"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @PostMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> join(@PathVariable Long id) {
        communityService.joinCommunity(id);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Leave a community",
            description = "Authenticated user leaves the specified community.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Left"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @PostMapping("/{id}/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> leave(@PathVariable Long id) {
        communityService.leaveCommunity(id);
        return ResponseEntity.ok().build();
    }

    // images

    @Operation(summary = "Upload community avatar (image)",
            description = "Upload a square avatar image for the community. Multipart form with a single 'file' field.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar uploaded",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityImageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()") // only owner
    public ResponseEntity<CommunityImageDTO> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(communityService.uploadAvatar(id, file));
    }


    @Operation(summary = "Upload community banner (image)",
            description = "Upload a wide banner image for the community. Multipart form with a single 'file' field.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banner uploaded",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityImageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PostMapping(value = "/{id}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()") // refine: only owner
    public ResponseEntity<CommunityImageDTO> uploadBanner(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(communityService.uploadBanner(id, file));
    }


    @Operation(summary = "Download community avatar (binary image)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar image stream",
                    content = {
                            @Content(mediaType = "image/jpeg", schema = @Schema(type = "string", format = "binary")),
                            @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}/avatar")
    public ResponseEntity<Resource> downloadAvatar(@PathVariable Long id) {
        Resource r = communityService.downloadAvatar(id);
        return ResponseEntity.ok().body(r); //TODO: add a content type to Community image so that it can be reported back to front when downloading(not priority)!
    }



    @Operation(summary = "Download community banner (binary image)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banner image stream",
                    content = {
                            @Content(mediaType = "image/jpeg", schema = @Schema(type = "string", format = "binary")),
                            @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}/banner")
    public ResponseEntity<Resource> downloadBanner(@PathVariable Long id) {
        Resource r = communityService.downloadBanner(id);
        return ResponseEntity.ok().body(r);
    }

    @Operation(summary = "Get avatar metadata")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar metadata",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityImageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}/avatar/meta")
    public ResponseEntity<CommunityImageDTO> avatarMeta(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getAvatarMeta(id));
    }


    @Operation(summary = "Get banner metadata")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banner metadata",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityImageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}/banner/meta")
    public ResponseEntity<CommunityImageDTO> bannerMeta(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getBannerMeta(id));
    }

}
