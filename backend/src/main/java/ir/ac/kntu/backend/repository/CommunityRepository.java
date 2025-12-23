package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends IBaseRepository<Community, Long> {

    Optional<Community> findByName(String name);

    Page<Community> findByOwner_Id(Long ownerId, Pageable pageable);

    // All communities a user is a member of (by user id)
    Page<Community> findByMembers_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"owner", "posts", "members"})
    Optional<Community> findWithPostsAndMembersById(Long id);
}
