package com.sjna.teamup.validator.validator;

import com.sjna.teamup.validator.constraint.UserNicknameConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class UserNicknameValidator implements ConstraintValidator<UserNicknameConstraint, String> {
    private static final String USER_NICKNAME_PATTERN = "^(?!_)(?=.*[a-zA-Z0-9가-힣_])[a-zA-Z0-9가-힣_]{2,16}(?<!_)$";
    private final MessageSource messageSource;

    @Override
    public boolean isValid(String userNickname, ConstraintValidatorContext context) {
        if(StringUtils.isBlank(userNickname) || !userNickname.matches(USER_NICKNAME_PATTERN)) {
            String errorMessage = messageSource.getMessage("constraint.user-nickname.pattern", null, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
