package com.sjna.teamup.common.controller.validator;

import com.sjna.teamup.common.controller.constraint.MapSizeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Map;

@RequiredArgsConstructor
public class MapSizeValidator implements ConstraintValidator<MapSizeConstraint, Map<String, String>> {

    private String message;
    private String[] params;
    private int min;
    private int max;
    private final MessageSource messageSource;

    @Override
    public void initialize(MapSizeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "error.map.size" : constraintAnnotation.message();
        this.params = constraintAnnotation.params();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Map<String, String> strMap, ConstraintValidatorContext context) {
        if(strMap == null || strMap.size() < this.min || strMap.size() > this.max) {
            String errorMessage = messageSource.getMessage(this.message, this.params, LocaleContextHolder.getLocale());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
