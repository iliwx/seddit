package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.PostAttachment;

import java.util.List;
import java.util.Optional;

public interface PostAttachmentRepository extends IBaseRepository<PostAttachment, Long> {

    List<PostAttachment> findByPost_Id(Long postId);

    // find single attachment ensuring it belongs to specific post (optional helper)
    Optional<PostAttachment> findByIdAndPost_Id(Long id, Long postId);


}
