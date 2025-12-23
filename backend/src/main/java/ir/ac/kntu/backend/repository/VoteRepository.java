package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.Vote;
import ir.ac.kntu.backend.model.VotableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByVotableTypeAndVotableIdAndUser_Id(VotableType votableType, Long votableId, Long userId);

    List<Vote> findByUser_IdAndVotableTypeAndVotableIdIn(Long userId, VotableType votableType, List<Long> votableIds);

    List<Vote> findByVotableTypeAndVotableId(VotableType votableType, Long votableId);
}
