package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.Comment;
import ir.ac.kntu.backend.model.Post;
import ir.ac.kntu.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends IBaseRepository<Comment, Long> {

    Page<Comment> findByPost_IdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    // For replies of a parent comment:
    List<Comment> findByParent_IdOrderByCreatedAtAsc(Long parentId);

    /**
     * Fetch immediate replies for a list of parent ids ordered by createdAt.
     * Using @EntityGraph to eager-load author to avoid N+1 when mapping.
     */
    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByParent_IdInOrderByCreatedAtAsc(List<Long> parentIds);

    Page<Comment> findByAuthor_IdOrderByCreatedAtDesc(Long id, Pageable commentsPage);

    // Optional: count immediate replies
    long countByParent_Id(Long parentId);

    // page top-level comments and fetch author to avoid N+1 when mapping
    @EntityGraph(attributePaths = {"author"})
    Page<Comment> findByPost_IdAndParentIsNullOrderByCreatedAtAsc(Long postId, Pageable pageable);

}
