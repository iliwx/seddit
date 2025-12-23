package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ICommentService {

    @Transactional
    CommentDTO.CommentThread createComment(Long postId, CommentDTO.CommentCreateRq rq);

    //user must be authorized
    @Transactional
    CommentDTO.Summary updateComment(Long commentId, CommentDTO.CommentUpdateRq rq);

    @Transactional
    boolean deleteComment(Long commentId);

    @Transactional(readOnly = true)
    Page<CommentDTO.CommentThread> getTopLevelCommentsWithReplies(Long postId, Pageable pageable, int replyLimit, int maxDepth);

    @Transactional(readOnly = true)
    CommentDTO.CommentThread getCommentThread(Long parentCommentId);

    // + CRUD!! and search?
    Page<CommentDTO.Summary> listUserComments(Long userId, Pageable pageable);
}
