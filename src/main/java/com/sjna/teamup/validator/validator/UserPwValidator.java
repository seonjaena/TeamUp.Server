package com.sjna.teamup.validator.validator;

import com.sjna.teamup.validator.VALID_REGEX;
import com.sjna.teamup.validator.constraint.UserPwConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class UserPwValidator implements ConstraintValidator<UserPwConstraint, String> {

    private String message;
    private final MessageSource messageSource;

    @Override
    public void initialize(UserPwConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "constraint.user-pw.pattern" : constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String userPw, ConstraintValidatorContext context) {
        if(userPw == null || !userPw.matches(VALID_REGEX.USER_PW.getRegexp())) {
            String errorMessage = messageSource.getMessage(this.message, null, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
