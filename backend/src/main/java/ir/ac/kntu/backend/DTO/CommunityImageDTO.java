package ir.ac.kntu.backend.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityImageDTO {

    private Long id;
    private Long size;
    private String url; // e.g. /api/communities/{id}/avatar or CDN URL
}
