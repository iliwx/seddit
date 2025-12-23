package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.VoteDTO;
import org.springframework.transaction.annotation.Transactional;

public interface IVotingService {

    @Transactional
    long vote(Long targetId, VoteDTO voteDTO);
}
