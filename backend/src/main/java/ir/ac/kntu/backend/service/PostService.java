package ir.ac.kntu.backend.service;

import ir.ac.kntu.backend.ContentTypeDetector;
import ir.ac.kntu.backend.CustomException;
import ir.ac.kntu.backend.DTO.PostDTO;
import ir.ac.kntu.backend.ThumbnailWorker;
import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.error.CommunityErrorCode;
import ir.ac.kntu.backend.error.PostAttachmentError;
import ir.ac.kntu.backend.error.PostErrorCode;
import ir.ac.kntu.backend.error.UserErrorCode;
import ir.ac.kntu.backend.iservice.IBeanMapper;
import ir.ac.kntu.backend.iservice.IPostService;
import ir.ac.kntu.backend.model.*;
import ir.ac.kntu.backend.repository.CommunityRepository;
import ir.ac.kntu.backend.repository.PostAttachmentRepository;
import ir.ac.kntu.backend.repository.PostRepository;
import ir.ac.kntu.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ir.ac.kntu.backend.service.ThumbnailService.THUMB_HEIGHT;
import static ir.ac.kntu.backend.service.ThumbnailService.THUMB_WIDTH;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService implements IPostService {

    //TODO: implement upvote downvote functionality for posts which needs change in pst model since we would need to track users' votes across the website
    //TODO: implement a search service for post and all other models required using the search package
    private final PostRepository postRepository;
    private final PostAttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final ContentTypeDetector contentTypeDetector;
    private final CommunityRepository communityRepository;
    private final IBeanMapper mapper;
    private final ThumbnailService thumbnailService;

    private static final long MAX_ATTACHMENT_BYTES = 20L * 1024L * 1024L; // 20 MB per file
    private final ThumbnailWorker thumbnailWorker;

    @Override
    @Transactional
    public PostDTO.PostCreateResponse createPost(PostDTO.PostCreateRequest rq, MultipartFile[] attachments) throws Exception {

        Long userId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();


        User author = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, userId.toString()));
        Community community = communityRepository.findById(rq.getCommunityId())
                .orElseThrow(() -> new CustomException(CommunityErrorCode.InvalidCommunityId, rq.getCommunityId().toString()));

        log.warn("{}ownerID:{}", userId.toString(), community.getOwner().getId());
        if (!community.getMembers().contains(author) && !community.getOwner().getId().equals(userId)) {
            throw new CustomException(CommunityErrorCode.UserNotMemberOfCommunity, rq.getCommunityId().toString());
        }

        Post post = Post.builder()
                .title(rq.getTitle())
                .content(rq.getContent())
                .community(community)
                .author(author)
                .build();
        post = postRepository.save(post);

        List<PostAttachment> createdAttachments = new ArrayList<>();
        if (attachments != null) {

            for (MultipartFile file : attachments) {
                if (file == null || file.isEmpty()) continue;
                if (file.getSize() > MAX_ATTACHMENT_BYTES) {
                    log.error("Attachment too large: {}", file.getOriginalFilename(), new IllegalArgumentException());
                    throw new CustomException(PostAttachmentError.FileTooLarge, file.getOriginalFilename());
                }

                // detect content type
                String detectedContentType = contentTypeDetector.detectContentType(file);

                String raw = Objects.requireNonNull(file.getOriginalFilename());
                String filename = UUID.randomUUID().toString().substring(0, 8) + "_" + raw;
                PostAttachment att = PostAttachment.builder()
                        .filename(filename)
                        .contentType(detectedContentType)
                        .size(file.getSize())
                        .data(file.getBytes())
                        .post(post)
                        .thumbnailStatus(ThumbnailStatus.PENDING)
                        .thumbnailVersion(0L)
                        .build();

                // uni-directional relationship creation before saving to DB
                post.addAttachment(att);
                createdAttachments.add(att);
            }
        }

        post = postRepository.saveAndFlush(post);

        List<PostDTO.AttachmentDTO> attachmentDTOs = new ArrayList<>();
        for (PostAttachment attachment : createdAttachments) {

            PostDTO.AttachmentDTO dto = mapper.toAttachmentDTO(attachment);
            attachmentDTOs.add(dto);

            if(attachment.getContentType() != null && (attachment.getContentType().startsWith("image/") ||
                    attachment.getContentType().startsWith("video/"))) {

                Long attachmentId = attachment.getId();
                thumbnailWorker.generateAsync(attachmentId);
            }
        }
        return new PostDTO.PostCreateResponse(post.getId(), post.getCreatedAt(), attachmentDTOs);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> listUserPosts(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByAuthor_IdOrderByCreatedAtDesc(userId, pageable);
        return posts.map(mapper::toPostDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> listPostsByCommunity(Long communityId, Pageable pageable) {
        Page<Post>  posts = postRepository.findByCommunity_IdOrderByCreatedAtDesc(communityId, pageable);
        return posts.map(mapper::toPostDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> listPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(mapper::toPostDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public PostDTO getPost(Long postId) {
        return mapper.toPostDTO(postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.PostNotFound, postId.toString())));
    }

    //    @PreAuthorize("isAuthenticated()") in controller absolutely necessary to be authenticated in controller
    @Transactional
    @Override
    public void deletePost(Long postId) {
        Long userId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();
        Post toBeDeleted = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.PostNotFound, postId.toString()));

        // must either be the OP, the community owner or (TODO: A MOD -> authorities: MOD-communityID)
        if(!userId.equals(toBeDeleted.getAuthor().getId()) || !userId.equals(toBeDeleted.getCommunity().getOwner().getId())) {
            throw new CustomException(PostErrorCode.Unauthorized, "Only the Original Author/Community Owner can Delete their Post");
        }
        postRepository.deleteById(postId);
    }

    // users may only update textual type posts,...for other types including attachments reupload is required
    //again absolutely necessary for the user to be authenticated by PreAuthorize
    @Transactional
    @Override
    public PostDTO updatePost(Long postId, PostDTO.PostUpdateRequest rq) {

        Long userId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();
        Post toBeUpdated = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.PostNotFound, postId.toString()));

        if(!userId.equals(toBeUpdated.getAuthor().getId())) {
            throw new CustomException(PostErrorCode.Unauthorized, "Only the Original Author can Update their Post");
        }

        mapper.updatePost(toBeUpdated, rq);
        postRepository.saveAndFlush(toBeUpdated);
        return mapper.toPostDTO(toBeUpdated);
    }

    @Transactional(readOnly = true)
    @Override
    public PostDTO.AttachmentDTO getAttachmentMeta(Long postId, Long attachmentId) {

        PostAttachment attachment = attachmentRepository.findByIdAndPost_Id(postId, attachmentId)
                .orElseThrow(() -> new CustomException(PostAttachmentError.AttachmentOrTheCorrespondingPostNotFound, "attachmentId: " + attachmentId.toString() + " PostId: " + postId.toString()));

        return mapper.toAttachmentDTO(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource streamAttachment(Long postId, Long attachmentId) {
        PostAttachment attachment = attachmentRepository.findByIdAndPost_Id(postId, attachmentId)
                .orElseThrow(() -> new CustomException(PostAttachmentError.AttachmentOrTheCorrespondingPostNotFound, "attachmentId: " + attachmentId.toString() + " PostId: " + postId.toString()));

        if (attachment.getData() == null) throw new CustomException(PostAttachmentError.PostAttachmentCorrupted, attachmentId.toString());

        return new ByteArrayResource(attachment.getData());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource streamAttachmentThumbnail(Long postId, Long attachmentId){
        PostAttachment attachment = attachmentRepository.findByIdAndPost_Id(postId, attachmentId)
                .orElseThrow(() -> new CustomException(PostAttachmentError.AttachmentOrTheCorrespondingPostNotFound, "attachmentId: " + attachmentId.toString() + " PostId: " + postId.toString()));

        if( attachment.getThumbnailData() != null && attachment.getThumbnailStatus() == ThumbnailStatus.DONE) {
            return new  ByteArrayResource(attachment.getThumbnailData());
        }

        try {
            return new ByteArrayResource(thumbnailService.createPlaceholderBytes(THUMB_WIDTH, THUMB_HEIGHT));
        } catch (IOException e) {
            throw new CustomException(PostAttachmentError.PostAttachmentThumbnailPlaceholderGenerationFailure, attachmentId.toString());
        }
    }

    @Override
    public boolean scheduleThumbnailGenerationForPost(Long postId) {
        List<PostAttachment> list = attachmentRepository.findByPost_Id(postId);
        for (PostAttachment a : list) {
            if (a.getThumbnailStatus() == ThumbnailStatus.PENDING || a.getThumbnailStatus() == ThumbnailStatus.FAILED) {
                thumbnailWorker.generateAsync(a.getId());
            }
        }
        return true;
    }
}
