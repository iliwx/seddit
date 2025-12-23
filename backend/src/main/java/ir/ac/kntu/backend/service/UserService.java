package ir.ac.kntu.backend.service;

import ir.ac.kntu.backend.CustomException;
import ir.ac.kntu.backend.DTO.*;
import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.error.UserErrorCode;
import ir.ac.kntu.backend.iservice.*;
import ir.ac.kntu.backend.model.Preferences;
import ir.ac.kntu.backend.model.ProfilePhoto;
import ir.ac.kntu.backend.model.RedisOTP;
import ir.ac.kntu.backend.model.User;
import ir.ac.kntu.backend.repository.*;
import ir.ac.kntu.backend.search.SearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

//TODO: make get services cacheable by redis and evict by update & delete
//TODO: add preferences services for modification of values

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final ICommunityService communityService;
    private final IPostService postService;
    private final ICommentService commentService;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final PasswordEncoder passwordEncoder;
    private final IBeanMapper beanMapper;
    private final RedisOTPRepository redisOTPRepository;

    private static final Pageable DEFAULT_POSTS_PAGEABLE = PageRequest.of(0, 10);
    private static final Pageable DEFAULT_COMMUNITIES_PAGEABLE = PageRequest.of(0, 10);
    private static final Pageable DEFAULT_COMMENTS_PAGEABLE = PageRequest.of(0, 10);
    private static final Pageable DEFAULT_NOTIFICATIONS_PAGEABLE = PageRequest.of(0, 10);
    private static final int OTP_LENGTH = 6;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    @Override
    public void requestOTP(UserDTO.LoginRqOTP loginRqOTP) {

        User user = userRepository.findByEmail(loginRqOTP.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound));

        if(user.isEnabled()){
            StringBuilder sb = new StringBuilder(OTP_LENGTH);
            for (int i = 0; i < OTP_LENGTH; i++) {sb.append(secureRandom.nextInt(10));}

            //TODO: replace with email sending Library later
            log.info("Request OTP for email {} with key {}", user.getEmail(), sb);

            redisOTPRepository.save(new RedisOTP(user.getEmail(), sb.toString()));
        }else {
            throw new CustomException(UserErrorCode.UserIsDisabled);
        }
    }

    @Transactional
    @Override
    public UserDTO.LoginRs verifyOTP(UserDTO.OtpVerifyRq suppliedOTP) {

        User user = userRepository.findByEmail(suppliedOTP.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound));

        if (!user.isEnabled()) {throw new CustomException(UserErrorCode.UserIsDisabled);}

        String key = user.getEmail();

        RedisOTP savedOtp = redisOTPRepository.findById(key)
                .orElseThrow(() -> new CustomException(UserErrorCode.InvalidOTP, key));

        if (!savedOtp.getOtp().equals(suppliedOTP.getOtp())) {

            int tries = savedOtp.getAttempts() + 1;
            if (tries >= 3) {
                redisOTPRepository.deleteById(key);
            } else {
                savedOtp.setAttempts(tries);
                redisOTPRepository.save(savedOtp);
            }
            throw new CustomException(UserErrorCode.InvalidOTP, key);
        }

        redisOTPRepository.deleteById(key);

        return new UserDTO.LoginRs(
                user.getId(),
                user.getUsername(),
                securityService.createToken(user.getId(), "ROLE_User")
        );
    }

    @Transactional
    @Override
    public UserDTO.LoginRs login(UserDTO.LoginRq loginRq) {

        final User user = userRepository.findByUsername(loginRq.getUsername())
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, loginRq.getUsername()));

        if (user.isEnabled() && passwordEncoder.matches(loginRq.getPassword(), user.getPassword())) {

            return new UserDTO.LoginRs(
                    user.getId(),
                    user.getUsername(),
                    securityService.createToken(user.getId(), "ROLE_User"));
        } else {
            throw new CustomException(UserErrorCode.InvalidUsernameOrPassword, loginRq.getUsername());
        }

    }

    @Transactional
    @Override
    public boolean logout() {
        return securityService.logout();
    }

    @Transactional
    @Override
    public UserDTO.UserCreateRs create(UserDTO.UserCreateRq registerRq) {

        final User user = beanMapper.toUser(registerRq);

        user.setPassword(passwordEncoder.encode(registerRq.getPassword()));
        user.setPreferences(new Preferences());
        User createdUser = userRepository.saveAndFlush(user);

        log.info("Registered User: {}", user);

        UserDTO.UserCreateRs rs = beanMapper.toUserCreateRs(createdUser);

        //TODO: add moderation roles associated with communities later, also needs change in verify OTP and the community model (Mods field)
        rs.setToken(securityService.createToken(user.getId(), "ROLE_User"));


        return rs;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users",key = "T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.principal")
    @Override
    public UserDTO.UserProfileRs profile(Pageable postsPageable, Pageable communitiesPageable, Pageable commentsPageable, Pageable notificationsPageable) {
        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        User user = userRepository.findById(actingUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound));

        UserDTO.UserProfileRs profileRs = beanMapper.toUserProfileRs(user);

        Pageable postsPage = (postsPageable != null) ? postsPageable : DEFAULT_POSTS_PAGEABLE;
        Pageable commsPage = (communitiesPageable != null) ? communitiesPageable : DEFAULT_COMMUNITIES_PAGEABLE;
        Pageable commentsPage = (commentsPageable != null) ? commentsPageable : DEFAULT_COMMENTS_PAGEABLE;
        Pageable notifPage = (notificationsPageable != null) ? notificationsPageable : DEFAULT_NOTIFICATIONS_PAGEABLE;

        profileRs.setPosts(postService.listUserPosts(user.getId(), postsPage).getContent());
        profileRs.setJoinedCommunities(communityService.listJoinedCommunities(user.getId(), commsPage).getContent());
        profileRs.setComments(commentService.listUserComments(user.getId(), commentsPage).getContent());
        profileRs.setOwnedCommunities(communityService.listOwnedCommunities(user.getId(), commsPage).getContent());
        //TODO: add Notifications later

        return profileRs;
    }

    @Transactional
    @Override
    public UserDTO.UserUpdate update(Long id, UserDTO.UserUpdate userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound));

        beanMapper.updateUser(user, userUpdate);
        User saved =  userRepository.saveAndFlush(user);

        return beanMapper.toUserUpdate(saved);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public UserDTO.UserStatus disableUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound));
        user.setEnabled(false);
        return beanMapper.toUserStatus(user);
    }

    @Transactional
    @Override
    public UserDTO.UserStatus enableUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound));
        user.setEnabled(true);
        return beanMapper.toUserStatus(user);
    }

    @Transactional
    @Override
    public UserDTO.UserCreateRs changePassword(UserDTO.ChangePassRq changePassRq) {

        Long userId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, userId.toString()));

        if(!passwordEncoder.matches(changePassRq.getPassword(), user.getPassword())){
            throw new CustomException(UserErrorCode.InvalidPassword);
        }

        user.setPassword(passwordEncoder.encode(changePassRq.getNewPassword()));

        userRepository.saveAndFlush(user);

        return beanMapper.toUserCreateRs(user);
    }

    //needs one to one controller
    @Transactional
    @Override
    public ProfilePhotoDTO uploadProfilePhoto(Long id, MultipartFile file) throws IOException {

        final User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, id.toString()));

        String raw = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String unique = UUID.randomUUID().toString().substring(0,8) + "_" + raw;

        ProfilePhoto photo = ProfilePhoto.builder()
                .filename(unique)
                .data(file.getBytes())
                .size(file.getSize())
                .build();

        user.setProfilePhoto(photo);
        userRepository.saveAndFlush(user);

        return new ProfilePhotoDTO(
                photo.getId(),
                file.getSize(),
                "/api/users/" + user.getId() + "/getPhoto"
        );
    }

    @Transactional
    @Override
    public void deleteProfilePhoto(Long id) {

        final User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, id.toString()));

        profilePhotoRepository.deleteById(user.getProfilePhoto().getId());
    }

    @Transactional(readOnly = true)
    @Override
    public SearchDTO.SearchRs<UserDTO.UserViewDTO> search(SearchDTO.SearchRq searchRq) {
        return SearchUtil.search(userRepository, searchRq, beanMapper::toUserViewDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public PreferencesDTO getPreferences() {
        Long userId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, userId.toString()));

        return beanMapper.toPreferencesDTO(user.getPreferences());

    }

    @Transactional
    @Override
    public PreferencesDTO updatePreferences(PreferencesDTO.PrefUpdateRq preferencesDto) {

        Long userId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, userId.toString()));

        Preferences preferences = user.getPreferences();
        beanMapper.updatePreferences(preferences, preferencesDto);
        userRepository.saveAndFlush(user);

        return beanMapper.toPreferencesDTO(preferences);
    }

    @Override
    public PreferencesDTO restoreDefaultPreferences() {
        Long userId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, userId.toString()));

        Preferences defaults = new Preferences();
        user.setPreferences(defaults);

        userRepository.saveAndFlush(user);

        return beanMapper.toPreferencesDTO(defaults);
    }

    @Override
    public NotificationDTO sendNotification(Long recipientId, NotificationDTO notificationDto) {
        return null;
    }

    @Override
    public List<NotificationDTO> listNotifications(Pageable pageable) {
        return List.of();
    }

    @Override
    public NotificationDTO markNotificationRead(Long notificationId) {
        return null;
    }

    @Override
    public void deleteNotification(Long notificationId) {

    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO.UserViewDTO getUserView(Long id, Pageable postsPageable, Pageable communitiesPageable, Pageable commentsPageable) {

        final User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, id.toString()));

        UserDTO.UserViewDTO userView = beanMapper.toUserViewDTO(user);

        ProfilePhotoDTO urlLess = userView.getProfilePhoto();
        urlLess.setUrl("/api/users/" + user.getId() + "/getPhoto");
        userView.setProfilePhoto(urlLess);

        Pageable postsPage = (postsPageable != null) ? postsPageable : DEFAULT_POSTS_PAGEABLE;
        Pageable commsPage = (communitiesPageable != null) ? communitiesPageable : DEFAULT_COMMUNITIES_PAGEABLE;
        Pageable commentsPage = (commentsPageable != null) ? commentsPageable : DEFAULT_COMMENTS_PAGEABLE;

        userView.setRecentPosts(postService.listUserPosts(user.getId(), postsPage).getContent());
        userView.setRecentJoinedCommunities(communityService.listJoinedCommunities(user.getId(), commsPage).getContent());
        userView.setRecentComments(commentService.listUserComments(user.getId(), commentsPage).getContent());

        return userView;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO.MinimalView getMinimalView(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, id.toString()));
        return beanMapper.toUserMinimalView(user);
    }


    @Transactional(readOnly = true)
    @Override
    public ProfilePhoto getProfilePhoto(Long id) {

        final User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, id.toString()));

        return user.getProfilePhoto();
    }
}
