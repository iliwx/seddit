package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.PostDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface IPostService {


    PostDTO.PostCreateResponse createPost(PostDTO.PostCreateRequest rq, MultipartFile[] attachments) throws Exception;

    Page<PostDTO> listUserPosts(Long userId, Pageable pageable);

    Page<PostDTO> listPostsByCommunity(Long communityId, Pageable pageable);

    Page<PostDTO> listPosts(Pageable pageable);

    PostDTO getPost(Long postId);

    void deletePost(Long postId);

    @Transactional
    PostDTO updatePost(Long postId, PostDTO.PostUpdateRequest rq);

    PostDTO.AttachmentDTO getAttachmentMeta(Long postId, Long attachmentId);

    Resource streamAttachment(Long postId, Long attachmentId);

    Resource streamAttachmentThumbnail(Long postId, Long attachmentId);

    boolean scheduleThumbnailGenerationForPost(Long postId);


}
