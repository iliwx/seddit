package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.*;
import ir.ac.kntu.backend.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IBeanMapper {

    CommentDTO.CommentThread toCommentThread(CommentDTO.Summary summary);
    User toUser(UserDTO.UserCreateRq userCreateRq);
    UserDTO.UserStatus toUserStatus(User user);
    PostDTO toPostDTO(Post post);
    UserDTO.UserCreateRs toUserCreateRs(User user);
    UserDTO.UserViewDTO toUserViewDTO(User user);
    Comment toComment(CommentDTO.CommentCreateRq rq);
    PreferencesDTO toPreferencesDTO(Preferences preferences);
    UserDTO.UserUpdate toUserUpdate(User user);



    default UserDTO.UserProfileRs toUserProfileRs(User user) {
        if (user == null) return null;
        UserDTO.UserProfileRs p = new UserDTO.UserProfileRs();
        p.setId(user.getId());
        p.setUsername(user.getUsername());
        p.setName(user.getName());
        p.setFamily(user.getFamily());
        p.setEmail(user.getEmail());
        p.setDescription(user.getDescription());
        p.setBirthDate(user.getBirthDate());
        p.setCreatedDate(user.getCreatedDate());

        if (user.getProfilePhoto() != null) {
            p.setProfilePhoto(new ProfilePhotoDTO(
                    user.getProfilePhoto().getId(),
                    user.getProfilePhoto().getSize(),
                    "/api/users/" + user.getId() + "/getPhoto"));
        }

        p.setPreferences(toPreferencesDTO(user.getPreferences()));
        // posts, joinedCommunities, ownedCommunities, comments, notifications
        return p;
    }


    default UserDTO.MinimalView toUserMinimalView(User user) {

        if (user == null) {
            return null;
        }

        UserDTO.MinimalView minimalView = new UserDTO.MinimalView();

        minimalView.setId(user.getId());
        minimalView.setUsername(user.getUsername());

        if (user.getProfilePhoto() == null) {
            minimalView.setProfilePhoto(null);

        }else{
            minimalView.setProfilePhoto(new ProfilePhotoDTO(
                    user.getProfilePhoto().getId(),
                    user.getProfilePhoto().getSize(),
                    "/api/users/" + user.getId() + "/getPhoto"));
        }
        return minimalView;
    }

    default PostDTO.AttachmentDTO toAttachmentDTO(PostAttachment postAttachment) {

        if ( postAttachment == null ) {
            return null;
        }

        PostDTO.AttachmentDTO dto = new PostDTO.AttachmentDTO();

        dto.setId(postAttachment.getId());
        dto.setFilename(postAttachment.getFilename());
        dto.setContentType(postAttachment.getContentType());
        dto.setSize(postAttachment.getSize());
        dto.setUrl("/api/posts/" + postAttachment.getPost().getId() + "/attachments/" + postAttachment.getId());
        dto.setThumbnailStatus(postAttachment.getThumbnailStatus());
        dto.setThumbnailVersion(postAttachment.getThumbnailVersion());
        dto.setThumbnailUrl("/api/posts/" + postAttachment.getPost().getId() + "/attachments/" + postAttachment.getId()
                + "/thumbnail?v=" + postAttachment.getThumbnailVersion());
        return dto;
    }

    default CommunityDTO.MinimalView toCommunityMinimalView(Community community) {

        if ( community == null ) {
            return null;
        }

        CommunityDTO.MinimalView minimalView = new CommunityDTO.MinimalView();

        minimalView.setId( community.getId() );
        minimalView.setName( community.getName() );
        minimalView.setAvatarImage( new CommunityImageDTO(
                community.getAvatarImage().getId(),
                community.getAvatarImage().getSize(),
                "/api/communities/" + community.getId() + "/avatar") );

        return minimalView;
    }

    default CommunityDTO toCommunityDTO(Community community) {
        if ( community == null ) {
            return null;
        }

        CommunityDTO communityDTO = new CommunityDTO();

        communityDTO.setId( community.getId() );
        communityDTO.setName( community.getName() );
        communityDTO.setDescription( community.getDescription() );
        communityDTO.setCreatedAt( community.getCreatedAt() );
        communityDTO.setMembers( community.getMembers() != null ? community.getMembers().size() : 0 );


        if (community.getAvatarImage() != null) {
            communityDTO.setAvatarImage( new CommunityImageDTO(
                    community.getAvatarImage().getId(),
                    community.getAvatarImage().getSize(),
                    "/api/communities/" + community.getId() + "/avatar"));
        }


        if (community.getBannerImage() != null) {
            communityDTO.setBannerImage( new CommunityImageDTO(
                    community.getBannerImage().getId(),
                    community.getBannerImage().getSize(),
                    "/api/communities/" + community.getId() + "/banner"
            ));
        }

        return communityDTO;
    }

    default CommentDTO.CommentThread toCommentThread(Comment c) {

        if ( c == null ) {
            return null;
        }

        CommentDTO.Summary summary = toCommentSummary(c);
        CommentDTO.CommentThread t = toCommentThread(summary);

        t.setReplies(new ArrayList<>());
        t.setMoreReplies(false); // when fetching subtree we return all replies so false (check for bugs and correctness later)
        return t;
    }

    default CommentDTO.Summary toCommentSummary(Comment c) {
        if ( c == null ) {
            return null;
        }

        CommentDTO.Summary s = new CommentDTO.Summary();
        s.setId(c.getId());
        s.setText(c.getText());
        s.setCreatedAt(c.getCreatedAt());
        s.setAuthor(toUserMinimalView(c.getAuthor()));
        s.setVotes(c.getVotes());
        s.setVersion(c.getVersion());
        s.setLastModifiedDate(c.getLastModifiedDate() != null ? c.getLastModifiedDate() : null);
        s.setParentId(c.getParent() == null ? null : c.getParent().getId());

        return s;
    }


    void updatePost(@MappingTarget Post dest, PostDTO.PostUpdateRequest src);
    void updateUser(@MappingTarget User dest, UserDTO.UserUpdate src);
    void updateComment(@MappingTarget Comment dest, CommentDTO.CommentUpdateRq src);
    void updatePreferences(@MappingTarget Preferences dest, PreferencesDTO.PrefUpdateRq src);
}
