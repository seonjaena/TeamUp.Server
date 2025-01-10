package com.sjna.teamup.common.controller.validator;

import com.sjna.teamup.common.controller.constraint.MapElementPatternConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Map;

@RequiredArgsConstructor
public class MapElementPatternValidator implements ConstraintValidator<MapElementPatternConstraint, Map<String, String>> {

    private String regexp;
    private String message;
    private String[] params;
    private final MessageSource messageSource;

    @Override
    public void initialize(MapElementPatternConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "error.map.pattern" : constraintAnnotation.message();
        this.params = constraintAnnotation.params();
        this.regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(Map<String, String> strList, ConstraintValidatorContext context) {
        for(Map.Entry<String, String> entry : strList.entrySet()) {
            String value = entry.getValue();
            if(StringUtils.isBlank(value) || !value.matches(regexp)) {
                String errorMessage = messageSource.getMessage(this.message, this.params, LocaleContextHolder.getLocale());
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorMessage)
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
