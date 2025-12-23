package ir.ac.kntu.backend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements IErrorCode {

    InvalidCommunityId(400),
    UserNotMemberOfCommunity(HttpStatus.FORBIDDEN.value()),
    OnlyOwnersMayModifyCommunities(HttpStatus.FORBIDDEN.value()),
    ImageResourceNotFound(HttpStatus.NOT_FOUND.value());

    // ------------------------------

    private final Integer httpStatusCode;

    @Override
    public String getCode() {
        return name();
    }
}