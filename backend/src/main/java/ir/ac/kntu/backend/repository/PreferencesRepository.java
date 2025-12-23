package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.Preferences;
import ir.ac.kntu.backend.model.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferencesRepository extends IBaseRepository<Preferences, Long> {
}
