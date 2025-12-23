package ir.ac.kntu.backend;

import jakarta.annotation.Nullable;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Component
public class ContentTypeDetector {

    private final Detector detector;

    public ContentTypeDetector() {
        this.detector = new DefaultDetector();
    }


    public String detectContentType(InputStream input) throws IOException {
        Objects.requireNonNull(input, "input stream is null");
        Metadata metadata = new Metadata();
        MediaType mediaType = detector.detect(input, metadata);
        return mediaType != null ? mediaType.toString() : "application/octet-stream";
    }

    public String detectContentType(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return detectContentType(is);
        }
    }

    public String detectContentType(byte[] data) throws IOException {
        try (InputStream is = new ByteArrayInputStream(data)) {
            return detectContentType(is);
        }
    }

    public String detectContentType(java.io.File file) throws IOException {
        try (InputStream is = new java.io.FileInputStream(file)) {
            return detectContentType(is);
        }
    }
}

