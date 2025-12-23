package ir.ac.kntu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.backend.DTO.PreferencesDTO;
import ir.ac.kntu.backend.DTO.ProfilePhotoDTO;
import ir.ac.kntu.backend.DTO.SearchDTO;
import ir.ac.kntu.backend.DTO.UserDTO;
import ir.ac.kntu.backend.iservice.IUserService;
import ir.ac.kntu.backend.model.ProfilePhoto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Tag(
        name = "User API",
        description = "Endpoints for user registration, login, profile management, and OTP flows"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;


    @Operation(summary = "Get a Profile Photo",
            description = "Return the binary image data for the specified user's profile photo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile photo binary stream", content = @Content),
            @ApiResponse(responseCode = "404", description = "Profile photo not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping(
            value = "/{id}/getPhoto",
            produces = {
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE
            }
    )
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<Resource> getProfilePhoto(@PathVariable Long id) {

        ProfilePhoto photo = userService.getProfilePhoto(id);

        ByteArrayResource resource = new ByteArrayResource(photo.getData());

        return ResponseEntity.ok()
                .contentLength(photo.getData().length)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(resource);
    }



    @Operation(summary = "Upload a Profile Photo",
            description = "Upload binary image data for the specified user's profile photo.")
    @PostMapping("/{id}/uploadPhoto")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<ProfilePhotoDTO> uploadProfilePhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(userService.uploadProfilePhoto(id, file));
    }


    @Operation(
            summary = "Delete user's profile photo",
            description = "Deletes the authenticated user's profile photo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile photo deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or photo not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @DeleteMapping("/{id}/photo")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<Void> deleteProfilePhoto(@PathVariable Long id) {
        userService.deleteProfilePhoto(id);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the given details and returns a JWT session token."
    )
    @PostMapping("/")
    public ResponseEntity<UserDTO.UserCreateRs> createUser(@Valid @RequestBody UserDTO.UserCreateRq userCreateRq) {
        return ResponseEntity.ok(userService.create(userCreateRq));
    }

    @Operation(
            summary = "Change a user's password",
            description = "Allows a Logged-in user to change the password. userId interpolated from Auth Token after sign-in"
    )
    @PostMapping("/changePassword")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO.UserCreateRs> changePassword(@Valid @RequestBody UserDTO.ChangePassRq changePassRq) {
        return ResponseEntity.ok(userService.changePassword(changePassRq));
    }


    @Operation(summary = "Get current user's profile",
            description = "Returns the authenticated user's own profile. You may provide paging query params for the embedded lists.")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO.UserProfileRs> profile(
            @RequestParam(value = "postsPage", required = false) Integer postsPage,
            @RequestParam(value = "postsSize", required = false) Integer postsSize,
            @RequestParam(value = "communitiesPage", required = false) Integer communitiesPage,
            @RequestParam(value = "communitiesSize", required = false) Integer communitiesSize,
            @RequestParam(value = "commentsPage", required = false) Integer commentsPage,
            @RequestParam(value = "commentsSize", required = false) Integer commentsSize,
            @RequestParam(value = "notificationsPage", required = false) Integer notificationsPage,
            @RequestParam(value = "notificationsSize", required = false) Integer notificationsSize
    ) {
        Pageable postsPageable = buildPageable(postsPage, postsSize, 0, 10);
        Pageable commsPageable = buildPageable(communitiesPage, communitiesSize, 0, 10);
        Pageable commentsPageable = buildPageable(commentsPage, commentsSize, 0, 10);
        Pageable notificationsPageable = buildPageable(notificationsPage, notificationsSize, 0, 10);

        return ResponseEntity.ok(userService.profile(postsPageable, commsPageable, commentsPageable, notificationsPageable));
    }

    @Operation(
            summary = "Get a user's public view",
            description = "Returns a user's view DTO including recent posts, joined communities and comments. " +
                    "Paging query params are optional and applied to the embedded lists."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User view returned", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}/view")
    public ResponseEntity<UserDTO.UserViewDTO> getUserView(
            @PathVariable Long id,
            @RequestParam(value = "postsPage", required = false) Integer postsPage,
            @RequestParam(value = "postsSize", required = false) Integer postsSize,
            @RequestParam(value = "communitiesPage", required = false) Integer communitiesPage,
            @RequestParam(value = "communitiesSize", required = false) Integer communitiesSize,
            @RequestParam(value = "commentsPage", required = false) Integer commentsPage,
            @RequestParam(value = "commentsSize", required = false) Integer commentsSize
    ) {
        Pageable postsPageable = buildPageable(postsPage, postsSize, 0, 10);
        Pageable commsPageable = buildPageable(communitiesPage, communitiesSize, 0, 10);
        Pageable commentsPageable = buildPageable(commentsPage, commentsSize, 0, 10);

        UserDTO.UserViewDTO userView = userService.getUserView(id, postsPageable, commsPageable, commentsPageable);
        return ResponseEntity.ok(userView);
    }

    @Operation(
            summary = "Get minimal user view",
            description = "Returns a minimal representation of the user (id, username, profile photo ref, etc.)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Minimal user view returned", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}/minimal")
    public ResponseEntity<UserDTO.MinimalView> getMinimalView(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getMinimalView(id));
    }


    @Operation(summary = "Update user details",
            description = "Admins may update any user; non-admins may update only their own.")
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<UserDTO.UserUpdate> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO.UserUpdate userUpdate) {
        return ResponseEntity.ok(userService.update(id, userUpdate));
    }


    @Operation(summary = "Search users",
            description = "Search users with query object.")
    @PostMapping("/search")
    public ResponseEntity<SearchDTO.SearchRs<UserDTO.UserViewDTO>> search(@RequestBody SearchDTO.SearchRq searchRq) {
        return ResponseEntity.ok(userService.search(searchRq));
    }


    @Operation(summary = "Get current user's preferences",
            description = "Returns the preferences for the authenticated user.")
    @GetMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PreferencesDTO> getPreferences() {
        return ResponseEntity.ok(userService.getPreferences());
    }


    @Operation(summary = "Update current user's preferences",
            description = "Updates preferences for the authenticated user (partial updates allowed).")
    @PutMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PreferencesDTO> updatePreferences(@Valid @RequestBody PreferencesDTO.PrefUpdateRq preferencesPrefUpdateRq) {
        return ResponseEntity.ok(userService.updatePreferences(preferencesPrefUpdateRq));
    }


    @Operation(summary = "Restore current user's preferences to default",
            description = "Resets preferences for the authenticated user to application defaults.")
    @PostMapping("/preferences/restore")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PreferencesDTO> restoreDefaultPreferences() {
        return ResponseEntity.ok(userService.restoreDefaultPreferences());
    }


    @Operation(
            summary = "Login with phone and password",
            description = "Authenticates the user and returns a session token."
    )
    @PostMapping("/logins")
    public ResponseEntity<UserDTO.LoginRs> login(@Valid @RequestBody UserDTO.LoginRq loginRq) {
        return ResponseEntity.ok(userService.login(loginRq));
    }


    @Operation(
            summary = "Logout current user",
            description = "Invalidates the current session token."
    )
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> logout() {
        return ResponseEntity.ok(userService.logout());
    }


    @Operation(
            summary = "Delete a user",
            description = "Users May Delete their account."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.ok("User Deleted Successfully");
    }


    @Operation(
            summary = "Disable a user",
            description = "users may disable only their own (locking them out)."
    )
    @PutMapping("/{id}/disable")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<UserDTO.UserStatus> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.disableUser(id));
    }

    @Operation(
            summary = "Enable a user",
            description = "users may enable only their own."
    )
    @PutMapping("/{id}/enable")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<UserDTO.UserStatus> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.enableUser(id));
    }


    @Operation(
            summary = "Request an OTP",
            description = "Generates a one-time password and stores it in Redis for the given Email."
    )
    @PostMapping("/otp/request")
    public ResponseEntity<Void> requestOtp(@Valid @RequestBody UserDTO.LoginRqOTP loginRqOTP) {
        userService.requestOTP(loginRqOTP);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Verify an OTP",
            description = "Checks the supplied OTP and logs in the user if valid."
    )
    @PostMapping("/otp/verify")
    public ResponseEntity<UserDTO.LoginRs> verifyOtp(@Valid @RequestBody UserDTO.OtpVerifyRq suppliedOTP) {

        return ResponseEntity.ok(userService.verifyOTP(suppliedOTP));
    }


    // helper Method
    private Pageable buildPageable(Integer page, Integer size, int defaultPage, int defaultSize) {
        int p = (page == null || page < 0) ? defaultPage : page;
        int s = (size == null || size <= 0) ? defaultSize : size;
        return PageRequest.of(p, s);
    }
}
