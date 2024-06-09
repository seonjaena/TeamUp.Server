package com.sjna.teamup.exception.handler;

import com.sjna.teamup.dto.response.ExceptionResponse;
import com.sjna.teamup.exception.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CommonRestExceptionHandler {

    private final MessageSource messageSource;

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = { UnAuthenticatedException.class, UsernameNotFoundException.class })
    public ResponseEntity unAuthenticatedException(UnAuthenticatedException e) {
        /**
         * TODO: 401 에러 코드는 인증에 대한 에러지만 code는 UNAUTHORIZED. 이 이유를 확인해보고 만약 내가 틀리게 사용하고 있다면 해결해야 함.
         * TODO: 401 에러 타입은 Unauthorized. 하지만 401은 인증에 대한 내용을 담고 싶기 때문에 임의로 타입 변경. 이게 옳은지 확인.
         */
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse("Unauthenticated", e.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = UnAuthorizedException.class)
    public ResponseEntity unAuthorizedException(UnAuthorizedException e) {
        // TODO: 403의 에러 메시지는 Forbidden. 하지만 403은 인가에 대한 내용을 담고 싶기 때문에 임의로 타입 변경. 이게 옳은지 확인. 또한 FORBIDDEN 의미 조사 필요.
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse("Unauthorized", e.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SendEmailFailureException.class)
    public ResponseEntity sendEmailFailureException(SendEmailFailureException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Failed To Send Email", e.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SendSMSFailureException.class)
    public ResponseEntity sendSMSFailureException(SendSMSFailureException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Failed To Send SMS", e.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = FileNotExistsException.class)
    public ResponseEntity fileNotExistsException(FileNotExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("File Not Exists", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String errorMsgCode = errors.get(0).getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMsgCode));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity constraintArgumentException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Bad Request", errorMessage));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = UserIdNotFoundException.class)
    public ResponseEntity userIdNotFoundException(UserIdNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("User Not Found", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public ResponseEntity handlerMethodValidationException(HandlerMethodValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Argument Error", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = EmptyFileException.class)
    public ResponseEntity emptyFileException(EmptyFileException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Empty File", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = FileSizeException.class)
    public ResponseEntity fileSizeException(FileSizeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("File Size", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadFileExtensionException.class)
    public ResponseEntity badFileExtensionException(BadFileExtensionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Bad File Extension", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ChangeProfileImageFailureException.class)
    public ResponseEntity changeProfileImageFailureException(ChangeProfileImageFailureException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Failed To Change Profile Image", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = AlreadyUserEmailExistsException.class)
    public ResponseEntity alreadyUserEmailExistsException(AlreadyUserEmailExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse("Email Already Exists", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = AlreadyUserPhoneExistsException.class)
    public ResponseEntity alreadyUserPhoneExistsException(AlreadyUserPhoneExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse("Phone Already Exists", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = AlreadyUserNicknameExistsException.class)
    public ResponseEntity alreadyUserNicknameExistsException(AlreadyUserNicknameExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse("Nickname Already Exists", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadVerificationCodeException.class)
    public ResponseEntity badVerificationCodeException(BadVerificationCodeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Bad Verification Code", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = UserYoungException.class)
    public ResponseEntity userYoungException(UserYoungException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("User Young", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = UserBirthDateNotExistsException.class)
    public ResponseEntity userBirthDateNotExistsException(UserBirthDateNotExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("User Birth Not Exists Date", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = UserPwPw2DifferentException.class)
    public ResponseEntity userPwPw2DifferentException(UserPwPw2DifferentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Pw Pw2 Different", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadUrlChangePwException.class)
    public ResponseEntity badUrlChangePwException(BadUrlChangePwException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse("Bad URL Change Password", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = AlreadyUsingPassword.class)
    public ResponseEntity alreadyUsingPassword(AlreadyUsingPassword e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Already Using Password", e.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = OriginPasswordIncorrect.class)
    public ResponseEntity originPasswordIncorrect(OriginPasswordIncorrect e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("Bad Origin Password", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = EncryptionException.class)
    public ResponseEntity encryptionException(EncryptionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Encryption Error", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = DecryptionException.class)
    public ResponseEntity decryptionException(DecryptionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Decryption Error", e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SerializationException.class)
    public ResponseEntity serializationException(SerializationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Failed To Serialize", e.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity unknownException(Exception e) {
        log.error(e.getMessage(), e);
        log.warn(e.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        messageSource.getMessage("error.common.500",
                                new String[] {},
                                LocaleContextHolder.getLocale())
                ));
    }

}
