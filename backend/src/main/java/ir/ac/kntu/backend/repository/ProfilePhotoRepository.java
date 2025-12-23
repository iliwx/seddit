package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoRepository extends IBaseRepository<ProfilePhoto, Long> {
    // Add custom search methods if you store photos by filename, owner, etc.
}
