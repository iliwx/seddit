package ir.ac.kntu.backend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements IErrorCode {

    PostNotFound(404),
    Unauthorized(HttpStatus.UNAUTHORIZED.value());

    // ------------------------------

    private final Integer httpStatusCode;

    @Override
    public String getCode() {
        return name();
    }
}
