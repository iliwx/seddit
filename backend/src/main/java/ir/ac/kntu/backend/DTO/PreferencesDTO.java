package ir.ac.kntu.backend.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesDTO {

    private Long id;
    private boolean emailNotifications;
    private String theme;      // e.g., "light" | "dark" | "system"
    private String language;   // e.g., "en"
    private boolean showNsfw;


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class PrefUpdateRq {
        private Boolean emailNotifications;
        private String theme;
        private String language;
        private Boolean showNsfw;
    }
}
