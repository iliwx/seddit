package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.CommunityDTO;
import ir.ac.kntu.backend.DTO.CommunityImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICommunityService {

    @Transactional(readOnly = true)
    Page<CommunityDTO> listAllCommunities(Pageable pageable);

    @Transactional(readOnly = true)
    Page<CommunityDTO> listJoinedCommunities(Long userId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<CommunityDTO> listOwnedCommunities(Long userId, Pageable pageable);

    CommunityDTO createCommunity(CommunityDTO.CommunityCreateRequest rq);

    CommunityDTO getCommunity(Long id);

    CommunityDTO updateCommunity(Long id, CommunityDTO.CommunityUpdateRequest rq);

    void deleteCommunity(Long id);

    void joinCommunity(Long communityId);
    void leaveCommunity(Long communityId);


    CommunityImageDTO uploadAvatar(Long communityId, MultipartFile file) throws IOException;

    CommunityImageDTO uploadBanner(Long communityId, MultipartFile file) throws IOException;

    Resource downloadAvatar(Long communityId);

    Resource downloadBanner(Long communityId);

    CommunityImageDTO getAvatarMeta(Long communityId);

    CommunityImageDTO getBannerMeta(Long communityId);


}
