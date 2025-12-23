package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends IBaseRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    /**
     * Load a user with the most relevant associations for a dashboard profile view.
     */
    @EntityGraph(attributePaths = {
            "profilePhoto",
            "address",
            "preferences",
            "joinedCommunities",
            "ownedCommunities",
            "posts",
            "comments",
            "notifications"
    })
    Optional<User> findWithDashboardById(Long id);

    @EntityGraph(attributePaths = {
            "profilePhoto",
            "joinedCommunities"
    })
    Optional<User> findWithProfileByUsername(String username);
}
