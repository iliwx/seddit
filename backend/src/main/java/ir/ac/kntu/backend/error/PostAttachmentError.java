package ir.ac.kntu.backend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostAttachmentError implements IErrorCode {

    FileTooLarge(HttpStatus.PAYLOAD_TOO_LARGE.value()),
    ThumbnailGenerationFailed(HttpStatus.INTERNAL_SERVER_ERROR.value()),
    AttachmentOrTheCorrespondingPostNotFound(HttpStatus.NOT_FOUND.value()),
    PostAttachmentCorrupted(HttpStatus.CONFLICT.value()),
    PostAttachmentThumbnailPlaceholderGenerationFailure(HttpStatus.INTERNAL_SERVER_ERROR.value());

    // ------------------------------

    private final Integer httpStatusCode;

    @Override
    public String getCode() {
        return name();
    }
}