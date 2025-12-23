package ir.ac.kntu.backend.service;

import ir.ac.kntu.backend.CustomException;
import ir.ac.kntu.backend.DTO.CommentDTO;
import ir.ac.kntu.backend.config.security.SecurityAuthenticationToken;
import ir.ac.kntu.backend.error.CommentErrorCode;
import ir.ac.kntu.backend.error.PostErrorCode;
import ir.ac.kntu.backend.error.UserErrorCode;
import ir.ac.kntu.backend.iservice.IBeanMapper;
import ir.ac.kntu.backend.iservice.ICommentService;
import ir.ac.kntu.backend.model.*;
import ir.ac.kntu.backend.repository.CommentRepository;
import ir.ac.kntu.backend.repository.PostRepository;
import ir.ac.kntu.backend.repository.UserRepository;
import ir.ac.kntu.backend.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final IBeanMapper mapper;

    // Safety caps
    private static final int MAX_NODES = 5000;   // maximum comments to fetch in a single thread
    private static final int MAX_ALLOWED_DEPTH = 50; // protective hard limit


    @Transactional
    @Override
    public CommentDTO.CommentThread createComment(Long postId, CommentDTO.CommentCreateRq rq) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.PostNotFound, postId.toString()));

        Comment comment = mapper.toComment(rq);

        if (rq.getParentId() != null) {
            Comment parent = commentRepository.findById(rq.getParentId())
                    .orElseThrow(() -> new CustomException(CommentErrorCode.CommentNotFound, rq.getParentId().toString()));

            if (parent.getId().equals(postId)) {
                throw new CustomException(CommentErrorCode.IllegalReply, postId.toString());
            }

            comment.setParent(parent);
        }


        comment.setPost(post);

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        User author = userRepository.findById(actingUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.UserNotFound, actingUserId.toString()));

        comment.setAuthor(author);

        Comment saved =  commentRepository.saveAndFlush(comment);
        
        return mapper.toCommentThread(saved);
    }




    //user must be authorized
    @Transactional
    @Override
    public CommentDTO.Summary updateComment(Long commentId, CommentDTO.CommentUpdateRq rq) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.CommentNotFound, commentId.toString()));

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        if(!comment.getAuthor().getId().equals(actingUserId)) throw new CustomException(CommentErrorCode.UnauthorizedEditByNonOwner, actingUserId.toString());

        if (comment.isDeleted()) throw new CustomException(CommentErrorCode.EditOfDeletedComment, commentId.toString());

        mapper.updateComment(comment, rq);

        return mapper.toCommentSummary(commentRepository.saveAndFlush(comment));

    }



    @Transactional
    @Override
    public boolean deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.CommentNotFound, commentId.toString()));

        Long actingUserId = ((SecurityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();

        if(!comment.getAuthor().getId().equals(actingUserId)) throw new CustomException(CommentErrorCode.UnauthorizedEditByNonOwner, actingUserId.toString());

        comment.setDeleted(true);
        comment.setText("[deleted]");

        commentRepository.saveAndFlush(comment);
        return true;
    }

    /**
     * Get paged top-level comments for a post and include replies up to maxDepth levels.
     *
     * @param postId     the post id
     * @param pageable   paging for top-level comments
     * @param replyLimit maximum immediate replies to attach to each parent (0 = none, Integer.MAX = all)
     * @param maxDepth   number of reply levels to fetch (1 = immediate children only, 2 = children + grandchildren, etc.)
     * @return Page of CommentDTO.CommentThread for top-level nodes, each populated with replies up to maxDepth
     */
    @Transactional(readOnly = true)
    @Override
    public Page<CommentDTO.CommentThread> getTopLevelCommentsWithReplies(Long postId, Pageable pageable, int replyLimit, int maxDepth) {

        if (maxDepth < 0) {
            throw new IllegalArgumentException("maxDepth must be >= 0");
        }
        if (replyLimit < 0) {
            throw new IllegalArgumentException("replyLimit must be >= 0");
        }
        if (maxDepth > MAX_ALLOWED_DEPTH) {
            throw new IllegalArgumentException("maxDepth too large (limit " + MAX_ALLOWED_DEPTH + ")");
        }

        Page<Comment> topLevel = commentRepository.findByPost_IdAndParentIsNullOrderByCreatedAtAsc(postId, pageable);

        Map<Long, CommentDTO.CommentThread> dtoById = new LinkedHashMap<>();
        List<Long> frontier = new ArrayList<>();

        for (Comment c : topLevel) {
            CommentDTO.CommentThread node = mapper.toCommentThread(c); // must map author, parentId but not replies
            node.setReplies(new ArrayList<>());
            node.setMoreReplies(false);
            dtoById.put(c.getId(), node);
            frontier.add(c.getId());
        }

        // bookkeeping for BFS traversal
        int currentDepth = 0;
        int totalCollected = dtoById.size();
        Set<Long> seen = new HashSet<>(dtoById.keySet());

        // BFS level-by-level until we reach maxDepth or no more children
        while (!frontier.isEmpty() && currentDepth < maxDepth) {
            currentDepth++;

            // Batch query immediate children of all ids in frontier
            List<Comment> children = commentRepository.findByParent_IdInOrderByCreatedAtAsc(frontier);

            if (children == null || children.isEmpty()) break;

            // group by parent id
            Map<Long, List<Comment>> childrenByParent = children.stream().collect(Collectors.groupingBy(c -> c.getParent().getId(), LinkedHashMap::new, Collectors.toList()));

            // prepare next frontier (ids of children we actually attached)
            List<Long> nextFrontier = new ArrayList<>();

            for (Map.Entry<Long, List<Comment>> e : childrenByParent.entrySet()) {
                Long parentId = e.getKey();
                List<Comment> allChildren = e.getValue();

                // ensure parent DTO exists (if parent not in dtoById it might be a deeper node we didn't include)
                CommentDTO.CommentThread parentDto = dtoById.get(parentId);

                if (parentDto == null) continue;

                boolean hasMore = (replyLimit > 0 && allChildren.size() > replyLimit);
                List<Comment> toAttach;
                if (replyLimit == 0) {
                    toAttach = Collections.emptyList();
                } else if (replyLimit >= allChildren.size()) {
                    toAttach = allChildren;
                } else {
                    toAttach = allChildren.stream().limit(replyLimit).collect(Collectors.toList());
                }

                for (Comment child : toAttach) {
                    Long cid = child.getId();
                    if (cid == null) continue;
                    if (seen.add(cid)) {
                        CommentDTO.CommentThread childDto = mapper.toCommentThread(child);
                        dtoById.put(cid, childDto);
                        parentDto.getReplies().add(childDto);
                        nextFrontier.add(cid);
                        totalCollected++;
                        if (totalCollected > MAX_NODES) {
                            throw new IllegalStateException("Comment thread too large (>" + MAX_NODES + " nodes)");
                        }
                    }
                }

                if (hasMore) {
                    parentDto.setMoreReplies(true);
                } else {
                    if (currentDepth >= maxDepth && !allChildren.isEmpty()) {
                        parentDto.setMoreReplies(true);
                    }
                }
            }

            frontier = nextFrontier;
        }

        // Now create Page<CommentDTO.CommentThread> by mapping topLevel content to their DTOs
        return topLevel.map(c -> dtoById.get(c.getId()));
    }

    /**
     * Return the subtree (thread) rooted at parentCommentId as a CommentDTO.Tree (root node).
     * This does a BFS-style multi-parent query to fetch only relevant descendants.
     *
     * @param parentCommentId id of the comment to serve as root of thread
     * @return CommentDTO.Tree for the parent (including nested replies)
     * @throws NoSuchElementException if parent is not found
     * @throws IllegalStateException for safety limit breaches
     */
    @Transactional(readOnly = true)
    @Override
    public CommentDTO.CommentThread getCommentThread(Long parentCommentId) {

        Comment root = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.CommentNotFound, parentCommentId.toString()));

        List<Comment> collected = new ArrayList<>();
        collected.add(root);

        // BFS queue of parent ids to find replies for
        Queue<Long> queue = new ArrayDeque<>();
        queue.add(root.getId());

        // Keep track of seen ids to avoid cycles
        Set<Long> seen = new HashSet<>();
        seen.add(root.getId());

        int depth = 0;
        while (!queue.isEmpty()) {

            if (depth++ > MAX_ALLOWED_DEPTH) {
                throw new IllegalStateException("Comment tree too deep (> " + MAX_ALLOWED_DEPTH + ")");
            }

            // gather next-level parent ids
            List<Long> parentIds = new ArrayList<>();
            while (!queue.isEmpty()) parentIds.add(queue.poll());

            // fetch immediate replies for all parentIds in one query
            List<Comment> replies = commentRepository.findByParent_IdInOrderByCreatedAtAsc(parentIds);

            if (replies.isEmpty()) break;

            // Add each reply if not seen; enqueue its id for next round
            for (Comment r : replies) {
                Long rid = r.getId();
                if (rid == null) continue;
                if (seen.add(rid)) {
                    collected.add(r);
                    queue.add(rid);
                }
            }

            if (collected.size() > MAX_NODES) {
                throw new IllegalStateException("Comment thread too large (>" + MAX_NODES + " nodes)");
            }
        }

        // Map id -> DTO node
        Map<Long, CommentDTO.CommentThread> dtoById = new HashMap<>(collected.size());
        for (Comment c : collected) {
            dtoById.put(c.getId(), mapper.toCommentThread(c));
        }

        // Attach children to their parent DTO (skip root parent == null)
        for (Comment c : collected) {
            CommentDTO.CommentThread node = dtoById.get(c.getId());
            if (c.getParent() == null) continue; // root or an orphan
            Long pId = c.getParent().getId();
            CommentDTO.CommentThread parentDto = dtoById.get(pId);
            if (parentDto != null) {
                parentDto.getReplies().add(node);
            }
        }

        return dtoById.get(root.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommentDTO.Summary> listUserComments(Long userId, Pageable pageable) {

        Page<Comment> commentPage = commentRepository.findByAuthor_IdOrderByCreatedAtDesc(userId, pageable);
        return commentPage.map(mapper::toCommentSummary);
    }


}
