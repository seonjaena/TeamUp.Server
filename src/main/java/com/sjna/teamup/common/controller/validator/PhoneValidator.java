package com.sjna.teamup.common.controller.validator;

import com.sjna.teamup.common.domain.VALID_REGEX;
import com.sjna.teamup.common.controller.constraint.PhoneConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {

    private String message;
    private final MessageSource messageSource;

    @Override
    public void initialize(PhoneConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "constraint.user-phone.pattern" : constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if(phone == null || !phone.matches(VALID_REGEX.PHONE.getRegexp())) {
            String errorMessage = messageSource.getMessage(this.message, null, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
