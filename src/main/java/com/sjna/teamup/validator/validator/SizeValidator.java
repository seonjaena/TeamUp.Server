package com.sjna.teamup.validator.validator;

import com.sjna.teamup.validator.constraint.SizeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class SizeValidator implements ConstraintValidator<SizeConstraint, String> {

    private String message;
    private String[] params;
    private int min;
    private int max;
    private final MessageSource messageSource;

    @Override
    public void initialize(SizeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "error.any.size" : constraintAnnotation.message();
        this.params = constraintAnnotation.params();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        if(str == null || str.length() < this.min || str.length() > this.max) {
            String errorMessage = messageSource.getMessage(this.message, this.params, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
