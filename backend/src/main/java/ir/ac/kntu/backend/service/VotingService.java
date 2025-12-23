package ir.ac.kntu.backend.service;

import ir.ac.kntu.backend.CustomException;
import ir.ac.kntu.backend.DTO.VoteDTO;
import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.error.CommentErrorCode;
import ir.ac.kntu.backend.error.PostErrorCode;
import ir.ac.kntu.backend.error.VoteErrorCode;
import ir.ac.kntu.backend.iservice.IVotingService;
import ir.ac.kntu.backend.model.*;
import ir.ac.kntu.backend.repository.CommentRepository;
import ir.ac.kntu.backend.repository.PostRepository;
import ir.ac.kntu.backend.repository.UserRepository;
import ir.ac.kntu.backend.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VotingService implements IVotingService {

    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * Generic vote entry point. Use VotableType.POST or VotableType.COMMENT.
     * value: +1 upvote, -1 downvote, 0 remove vote
     * Returns new aggregate votes for the target.
     */
    @Transactional
    @Override
    public long vote(Long targetId, VoteDTO voteDTO) {


        int value = voteDTO.getValue();
        VotableType type = voteDTO.getType();

        if (!(value == -1 || value == 0 || value == 1)) {
            throw new CustomException(VoteErrorCode.IllegalVoteValue, "Vote value must be -1, 0, or +1");
        }

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        Votable target = loadVotable(voteDTO.getType(), targetId);

        Optional<Vote> existingOpt = voteRepository.findByVotableTypeAndVotableIdAndUser_Id(voteDTO.getType(), targetId, actingUserId);
        int delta = 0;

        if (existingOpt.isPresent()) {
            Vote existing = existingOpt.get();
            int existingValue = existing.getValue();

            if (value == 0) {
                voteRepository.delete(existing);
                delta = -existingValue;
            } else if (existingValue != value) {

                // flip existing vote
                existing.setValue(value);
                voteRepository.saveAndFlush(existing);
                delta = value - existingValue; // +/-2
            }
        } else {
            if (value != 0) {

                Vote v = Vote.builder()
                        .votableType(type)
                        .votableId(targetId)
                        .user(userRepository.getReferenceById(actingUserId))
                        .value(value)
                        .build();
                voteRepository.saveAndFlush(v);
                delta = value;
            }
        }

        if (delta != 0) {
            // update to aggregate votes on the target
            target.setVotes(target.getVotes() + delta);

            if (type == VotableType.COMMENT) {
                commentRepository.saveAndFlush((Comment) target);
            } else if (type == VotableType.POST) {
                postRepository.saveAndFlush((Post) target);
            } else {
                throw new IllegalStateException("Unsupported votable type: " + type);
            }
        }

        return target.getVotes();
    }

    private Votable loadVotable(VotableType type, Long id) {

        if (type == VotableType.COMMENT) {
            return commentRepository.findById(id)
                    .orElseThrow(() -> new CustomException(CommentErrorCode.CommentNotFound,"Comment not found: " + id));
        } else if (type == VotableType.POST) {
            return postRepository.findById(id)
                    .orElseThrow(() -> new CustomException(PostErrorCode.PostNotFound,"Post not found: " + id));
        }
        throw new IllegalArgumentException("Unknown votable type: " + type);
    }
}
