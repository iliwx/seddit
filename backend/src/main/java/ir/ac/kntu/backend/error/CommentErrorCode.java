package ir.ac.kntu.backend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements IErrorCode {

    CommentNotFound(404),
    IllegalReply(HttpStatus.FORBIDDEN.value()),
    UnauthorizedEditByNonOwner(HttpStatus.UNAUTHORIZED.value()),
    EditOfDeletedComment(HttpStatus.UNAUTHORIZED.value());


    // ------------------------------

    private final Integer httpStatusCode;

    @Override
    public String getCode() {
        return name();
    }
}
