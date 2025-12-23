package ir.ac.kntu.backend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements IErrorCode {

	Unauthorized(HttpStatus.UNAUTHORIZED.value()),
	UserNotFound(404),
	UnregisteredUserOrInvalidPass(400),
	DuplicateUsername(400),
	InvalidUsernameOrPassword(400),
	InvalidOTP(400),
	UserIsDisabled(400),
    InvalidPassword(400);

	// ------------------------------

	private final Integer httpStatusCode;

	@Override
	public String getCode() {
		return name();
	}
}
