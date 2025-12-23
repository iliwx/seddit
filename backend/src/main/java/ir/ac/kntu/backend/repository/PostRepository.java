package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.Post;
import ir.ac.kntu.backend.model.Community;
import ir.ac.kntu.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends IBaseRepository<Post, Long> {

    Page<Post> findByAuthor_IdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    List<Post> findByCommunityOrderByCreatedAtDesc(Community community);

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    // fetch post with comments and author for detailed view
    @EntityGraph(attributePaths = {"comments", "author", "community"})
    Optional<Post> findWithCommentsById(Long id);

    // recent posts overall (use pageable in service)
    List<Post> findTop20ByOrderByCreatedAtDesc();

    Page<Post> findByCommunity_IdOrderByCreatedAtDesc(Long communityId, Pageable pageable);
}
