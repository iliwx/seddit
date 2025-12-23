package ir.ac.kntu.backend.iservice;

import ir.ac.kntu.backend.DTO.PreferencesDTO;

public interface IPreferencesService {

    /* Preferences updates */
    PreferencesDTO update(Long id, PreferencesDTO preferencesDto);

    PreferencesDTO get(Long id);
}
