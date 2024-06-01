package com.sjna.teamup.validator.validator;

import com.sjna.teamup.validator.constraint.UserIdConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class UserIdValidator implements ConstraintValidator<UserIdConstraint, String> {
    private static final String USER_ID_PATTERN = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
    private final MessageSource messageSource;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        if(StringUtils.isBlank(userId) || !userId.matches(USER_ID_PATTERN)) {
            String errorMessage = messageSource.getMessage("constraint.user-id.pattern", null, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
