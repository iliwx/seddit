package ir.ac.kntu.backend.config;


import ir.ac.kntu.backend.DTO.ErrorDTO;
import ir.ac.kntu.backend.CustomException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDTO.GeneralRs> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(
                new ErrorDTO.GeneralRs(ex.getCode().getCode(), ex.getDescription()),
                HttpStatus.valueOf(ex.getCode().getHttpStatusCode())
        );
    }

    // ---------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO.GeneralRs> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        final List<ErrorDTO.FieldDTO> errorFieldDTOS = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorDTO.FieldDTO(fieldError.getField(), fieldError.getCode()))
                .collect(Collectors.toList());

        ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(objectError -> new ErrorDTO.FieldDTO(null, objectError.getCode()))
                .collect(Collectors.toCollection(() -> errorFieldDTOS));

        return new ResponseEntity<>(
                new ErrorDTO.GeneralRs("InputValidationError").addFields(errorFieldDTOS), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO.GeneralRs> handleConstraintViolationException(ConstraintViolationException ex) {
        final List<ErrorDTO.FieldDTO> fields = ex.getConstraintViolations().stream()
                .map(cv -> new ErrorDTO.FieldDTO(
                        cv.getPropertyPath().toString(),
                        cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                new ErrorDTO.GeneralRs("InputValidationError").addFields(fields), HttpStatus.BAD_REQUEST);
    }

    // ---------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO.GeneralRs> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("@ControllerAdvice - AccessDenied");
        return new ResponseEntity<>(new ErrorDTO.GeneralRs("AccessDenied"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO.GeneralRs> handleException(Exception ex) {
        log.error("@ControllerAdvice - General Exception:", ex);
        return new ResponseEntity<>(new ErrorDTO.GeneralRs("Unknown"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
