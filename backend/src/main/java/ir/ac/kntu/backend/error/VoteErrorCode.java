package ir.ac.kntu.backend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VoteErrorCode implements IErrorCode {

    IllegalVoteValue(HttpStatus.FORBIDDEN.value());

    // ------------------------------

    private final Integer httpStatusCode;

    @Override
    public String getCode() {
        return name();
    }
}