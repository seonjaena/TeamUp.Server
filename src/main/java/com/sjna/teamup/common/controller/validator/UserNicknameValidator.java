package com.sjna.teamup.common.controller.validator;

import com.sjna.teamup.common.domain.VALID_REGEX;
import com.sjna.teamup.common.controller.constraint.UserNicknameConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class UserNicknameValidator implements ConstraintValidator<UserNicknameConstraint, String> {
    private String message;
    private final MessageSource messageSource;

    @Override
    public void initialize(UserNicknameConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "constraint.user-nickname.pattern" : constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String userNickname, ConstraintValidatorContext context) {
        if(userNickname == null || !userNickname.matches(VALID_REGEX.USER_NICKNAME.getRegexp())) {
            String errorMessage = messageSource.getMessage(this.message, null, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
