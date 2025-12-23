package ir.ac.kntu.backend.service;

import ir.ac.kntu.backend.ContentTypeDetector;
import ir.ac.kntu.backend.CustomException;
import ir.ac.kntu.backend.DTO.CommunityDTO;
import ir.ac.kntu.backend.DTO.CommunityImageDTO;
import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.error.CommunityErrorCode;
import ir.ac.kntu.backend.error.UserErrorCode;
import ir.ac.kntu.backend.iservice.IBeanMapper;
import ir.ac.kntu.backend.iservice.ICommunityService;
import ir.ac.kntu.backend.model.Community;
import ir.ac.kntu.backend.model.CommunityImage;
import ir.ac.kntu.backend.model.User;
import ir.ac.kntu.backend.repository.CommunityImageRepository;
import ir.ac.kntu.backend.repository.CommunityRepository;
import ir.ac.kntu.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommunityService implements ICommunityService {

    //TODO: PreAuthorize so that only the owner can change avatar and banners

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityImageRepository imageRepository;
    private final IBeanMapper mapper;

    private static final Set<String> ALLOWED_IMAGE_MIMES = Set.of("image/jpeg", "image/pjpeg", "image/png", "image/gif",
            "image/webp", "image/bmp", "image/x-icon");

    // Simple validation constants
    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L; // 10 MB default
    private final IBeanMapper iBeanMapper;
    private final ContentTypeDetector contentTypeDetector;


    @Transactional(readOnly = true)
    @Override
    public Page<CommunityDTO> listAllCommunities(Pageable pageable) {
        Page<Community> communityPage = communityRepository.findAll(pageable);
        return communityPage.map(mapper::toCommunityDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommunityDTO> listJoinedCommunities(Long userId, Pageable pageable) {
        Page<Community> page = communityRepository.findByMembers_Id(userId, pageable);
        return page.map(mapper::toCommunityDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommunityDTO> listOwnedCommunities(Long userId, Pageable pageable) {
        Page<Community> page = communityRepository.findByOwner_Id(userId, pageable);
        return page.map(mapper::toCommunityDTO);
    }

    // user must be authenticated first
    @Transactional
    @Override
    public CommunityDTO createCommunity(CommunityDTO.CommunityCreateRequest rq) {

        Long ownerId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, ownerId.toString()));

        Community community = Community.builder()
                .name(StringUtils.cleanPath(rq.getName()))
                .description(rq.getDescription())
                .owner(owner)
                .build();

        return mapper.toCommunityDTO(communityRepository.saveAndFlush(community));
    }

    @Transactional(readOnly = true)
    @Override
    public CommunityDTO getCommunity(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, id.toString()));
        return mapper.toCommunityDTO(community);
    }

    // user must be authenticated
    @Transactional
    @Override
    public CommunityDTO updateCommunity(Long id, CommunityDTO.CommunityUpdateRequest rq) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, id.toString()));

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        if (!community.getOwner().getId().equals(actingUserId)) {
            throw new CustomException(CommunityErrorCode.OnlyOwnersMayModifyCommunities);
        }

        if (rq.getName() != null && !rq.getName().isBlank()) community.setName(StringUtils.cleanPath(rq.getName()));
        if (rq.getDescription() != null) community.setDescription(rq.getDescription());

        return mapper.toCommunityDTO(communityRepository.saveAndFlush(community));
    }

    @Transactional
    @Override
    public void deleteCommunity(Long id) {

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, id.toString()));

        if(community.getOwner().getId().equals(actingUserId)){
            throw new CustomException(CommunityErrorCode.OnlyOwnersMayModifyCommunities);
        }

        communityRepository.delete(community);
    }

    @Transactional
    @Override
    public void joinCommunity(Long communityId) {

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));

        User user = userRepository.findById(actingUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, actingUserId.toString()));

        if (!community.getMembers().contains(user)) {
            community.getMembers().add(user);
            communityRepository.saveAndFlush(community);
        }
    }

    @Transactional
    @Override
    public void leaveCommunity(Long communityId) {

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));

        User user = userRepository.findById(actingUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, actingUserId.toString()));

        if (community.getMembers().contains(user)) {
            community.getMembers().remove(user);
            communityRepository.saveAndFlush(community);
        }
    }

    @Override
    @Transactional
    public CommunityImageDTO uploadAvatar(Long communityId, MultipartFile file) throws IOException {

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));

        if (!community.getOwner().getId().equals(actingUserId)) {
            throw new CustomException(CommunityErrorCode.OnlyOwnersMayModifyCommunities);
        }
        return uploadImage(communityId, file, true);
    }

    @Override
    @Transactional
    public CommunityImageDTO uploadBanner(Long communityId, MultipartFile file) throws IOException {
        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));

        if (!community.getOwner().getId().equals(actingUserId)) {
            throw new CustomException(CommunityErrorCode.OnlyOwnersMayModifyCommunities);
        }
        return uploadImage(communityId, file, false);
    }

    @Override
    @Transactional
    public Resource downloadAvatar(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));
        CommunityImage img = community.getAvatarImage();
        if (img == null || img.getData() == null) {
            throw new CustomException(CommunityErrorCode.ImageResourceNotFound, "Image Resource Not Found For Community With ID: " + communityId);
        }
        return new ByteArrayResource(img.getData());
    }

    @Override
    @Transactional
    public Resource downloadBanner(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));
        CommunityImage img = community.getBannerImage();
        if (img == null || img.getData() == null) {
            throw new CustomException(CommunityErrorCode.ImageResourceNotFound, "Image Resource Not Found For Community With ID: " + communityId);
        }
        return new ByteArrayResource(img.getData());
    }

    @Override
    @Transactional
    public CommunityImageDTO getAvatarMeta(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));
        CommunityImage img = community.getAvatarImage();
        return img == null ? null : toCommunityImageDTO(img, communityId, true);
    }

    @Override
    @Transactional
    public CommunityImageDTO getBannerMeta(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));
        CommunityImage img = community.getBannerImage();
        return img == null ? null : toCommunityImageDTO(img, communityId, false);
    }



    // ------------------------------------- HELPER METHODS -------------------------------------------------------------

    private CommunityImageDTO uploadImage(Long communityId, MultipartFile file, boolean avatar) throws IOException {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, communityId.toString()));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("file too large");
        }
        String contentType = contentTypeDetector.detectContentType(file);
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("only image/* content allowed for community images");
        }

        String raw = StringUtils.cleanPath(Objects.toString(file.getOriginalFilename(), ""));
        String filename = java.util.UUID.randomUUID().toString().substring(0, 8) + "_" + raw;

        CommunityImage image = CommunityImage.builder()
                .filename(filename)
                .size(file.getSize())
                .data(file.getBytes())
                .build();

        image = imageRepository.save(image);

        if (avatar) {
            community.setAvatarImage(image);
        } else {
            community.setBannerImage(image);
        }
        communityRepository.saveAndFlush(community);

        return toCommunityImageDTO(image, communityId, avatar);
    }

    private CommunityImageDTO toCommunityImageDTO(CommunityImage img, Long communityId, boolean avatar) {

        String url = avatar ? ("/api/communities/" + communityId + "/avatar")
                : ("/api/communities/" + communityId + "/banner");

        return new CommunityImageDTO(img.getId(), img.getSize(), url);
    }
}
