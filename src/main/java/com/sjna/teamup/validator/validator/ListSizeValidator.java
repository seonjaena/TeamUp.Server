package com.sjna.teamup.validator.validator;

import com.sjna.teamup.validator.constraint.ListSizeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.List;

@RequiredArgsConstructor
public class ListSizeValidator implements ConstraintValidator<ListSizeConstraint, List<String>> {

    private String message;
    private String[] params;
    private int min;
    private int max;
    private final MessageSource messageSource;

    @Override
    public void initialize(ListSizeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "error.list.size" : constraintAnnotation.message();
        this.params = constraintAnnotation.params();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(List<String> strList, ConstraintValidatorContext context) {
        if(strList == null || strList.size() < this.min || strList.size() > this.max) {
            String errorMessage = messageSource.getMessage(this.message, this.params, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
