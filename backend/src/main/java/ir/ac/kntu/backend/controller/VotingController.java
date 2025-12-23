package ir.ac.kntu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.backend.DTO.VoteDTO;
import ir.ac.kntu.backend.iservice.IVotingService;
import ir.ac.kntu.backend.model.VotableType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Voting API", description = "Endpoints for voting on posts and comments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class VotingController {

    private final IVotingService votingService;

    @Operation(
            summary = "Vote on a post",
            description = "Upvote (+1), downvote (-1) or remove vote (0) for a post specified by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "New vote count returned",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid vote value or bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized / authentication required", content = @Content)
    })
    @PostMapping("/posts/{id}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> votePost(@PathVariable Long id, int value) {
        return ResponseEntity.ok(votingService.vote(id, new VoteDTO(VotableType.POST,value)));
    }



    @Operation(
            summary = "Vote on a comment",
            description = "Upvote (+1), downvote (-1) or remove vote (0) for a comment specified by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "New vote count returned",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid vote value or bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized / authentication required", content = @Content)
    })
    @PostMapping("/comments/{id}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> voteComment(@PathVariable Long id, int value) {
        return ResponseEntity.ok(votingService.vote(id, new VoteDTO(VotableType.COMMENT,value)));
    }
}
