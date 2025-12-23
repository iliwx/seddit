package ir.ac.kntu.backend.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePhotoDTO {

    private Long id;
    private long size;
    private String url; // endpoint or public URL to download/preview
}

