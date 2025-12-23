package ir.ac.kntu.backend.iservice;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface IThumbnailService {
    @Transactional
    boolean generateThumbnailForAttachmentSync(Long attachmentId) throws Exception;

    byte[] createPlaceholderBytes(int w, int h) throws IOException;

    byte[] generateImageThumbnail(byte[] original, int targetW, int targetH) throws IOException;
}
