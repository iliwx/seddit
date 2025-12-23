package ir.ac.kntu.backend.service;

import ir.ac.kntu.backend.CustomException;
import ir.ac.kntu.backend.error.PostAttachmentError;
import ir.ac.kntu.backend.iservice.IThumbnailService;
import ir.ac.kntu.backend.model.PostAttachment;
import ir.ac.kntu.backend.model.ThumbnailStatus;
import ir.ac.kntu.backend.repository.PostAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThumbnailService implements IThumbnailService {

    //TODO: bug hunting!
    private final PostAttachmentRepository attachmentRepository;

    public static final int THUMB_WIDTH = 320;
    public static final int THUMB_HEIGHT = 180;



    @Transactional
    @Override
    public boolean generateThumbnailForAttachmentSync(Long attachmentId) throws Exception {

        PostAttachment att = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new CustomException(PostAttachmentError.AttachmentOrTheCorrespondingPostNotFound, attachmentId.toString()));

        att.setThumbnailStatus(ThumbnailStatus.IN_PROGRESS);
        attachmentRepository.saveAndFlush(att);

        // image
        if (att.getContentType() != null && att.getContentType().startsWith("image/")) {
            try {
                byte[] thumb = generateImageThumbnail(att.getData(), THUMB_WIDTH, THUMB_HEIGHT);
                att.setThumbnailData(thumb);
                att.setThumbnailSize((long) thumb.length);
                att.setThumbnailStatus(ThumbnailStatus.DONE);
                att.setThumbnailVersion(att.getThumbnailVersion() + 1);
                attachmentRepository.saveAndFlush(att);
                return true;
            } catch (Exception ex) {
                att.setThumbnailStatus(ThumbnailStatus.FAILED);
                attachmentRepository.saveAndFlush(att);
                throw new CustomException(PostAttachmentError.ThumbnailGenerationFailed, ex.getMessage());
            }
        }

        // video -> extract frame using ffmpeg binary (must be installed on host)
        if (att.getContentType() != null && att.getContentType().startsWith("video/")) {
            Path tmpVideo = Files.createTempFile("video-", "-" + att.getId());
            try {
                Files.write(tmpVideo, att.getData());
                Path tmpThumb = Files.createTempFile("thumb-", ".jpg");
                ProcessBuilder pb = new ProcessBuilder(
                        "ffmpeg", "-y", "-i", tmpVideo.toString(),
                        "-ss", "00:00:01.000", "-vframes", "1", "-q:v", "2", tmpThumb.toString()
                );
                pb.redirectErrorStream(true);
                Process p = pb.start();
                int exit = p.waitFor();

                if (exit != 0) {
                    att.setThumbnailStatus(ThumbnailStatus.FAILED);
                    attachmentRepository.saveAndFlush(att);
                    return false;
                }
                byte[] thumbBytes = Files.readAllBytes(tmpThumb);
                att.setThumbnailData(thumbBytes);
                att.setThumbnailSize((long) thumbBytes.length);
                att.setThumbnailStatus(ThumbnailStatus.DONE);
                att.setThumbnailVersion(att.getThumbnailVersion() + 1);
                attachmentRepository.saveAndFlush(att);
                try { Files.deleteIfExists(tmpThumb); } catch (Exception ignore) {}
                return true;
            } finally {
                try { Files.deleteIfExists(tmpVideo); } catch (Exception ignore) {}
            }
        }

        att.setThumbnailStatus(ThumbnailStatus.FAILED);
        attachmentRepository.saveAndFlush(att);
        return false;
    }


    @Override
    public byte[] createPlaceholderBytes(int w, int h) throws IOException {

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setPaint(Color.LIGHT_GRAY);
        g.fillRect(0, 0, w, h);
        g.setPaint(Color.DARK_GRAY);
        g.drawString("processing...", 10, h / 2);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }

    @Override
    public byte[] generateImageThumbnail(byte[] original, int targetW, int targetH) throws IOException {

        ByteArrayInputStream in = new ByteArrayInputStream(original);
        BufferedImage src = ImageIO.read(in);
        if (src == null) throw new IOException("Cannot read image");
        int w = src.getWidth();
        int h = src.getHeight();
        double scale = Math.min((double) targetW / w, (double) targetH / h);
        int nw = Math.max(1, (int) (w * scale));
        int nh = Math.max(1, (int) (h * scale));
        BufferedImage dst = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dst, "jpg", baos);
        return baos.toByteArray();
    }
}
