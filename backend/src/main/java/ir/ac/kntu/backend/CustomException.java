package ir.ac.kntu.backend;


import ir.ac.kntu.backend.error.IErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final IErrorCode code;
    private final String description;

    // ------------------------------

    public CustomException (IErrorCode code) {
        this(code, null);
    }
}