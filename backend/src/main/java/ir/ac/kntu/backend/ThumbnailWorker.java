package ir.ac.kntu.backend;

import ir.ac.kntu.backend.error.PostAttachmentError;
import ir.ac.kntu.backend.iservice.IPostService;
import ir.ac.kntu.backend.model.PostAttachment;
import ir.ac.kntu.backend.model.ThumbnailStatus;
import ir.ac.kntu.backend.repository.PostAttachmentRepository;
import ir.ac.kntu.backend.service.ThumbnailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbnailWorker {

    private final ThumbnailService thumbnailService;



    @Async("thumbnailExecutor")
    public void generateAsync(Long attachmentId) {
        try {
            thumbnailService.generateThumbnailForAttachmentSync(attachmentId);
        } catch (Exception ex) {
            log.error("Thumbnail Generation Failed: {}",ex.getMessage(), new CustomException(PostAttachmentError.ThumbnailGenerationFailed));
        }
    }
}