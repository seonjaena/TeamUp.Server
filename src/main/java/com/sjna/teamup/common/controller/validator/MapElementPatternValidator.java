package com.sjna.teamup.common.controller.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjna.teamup.common.controller.constraint.MapElementPatternConstraint;
import com.sjna.teamup.common.domain.ValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;

public class MapElementPatternValidator implements ConstraintValidator<MapElementPatternConstraint, Map<String, String>> {

    private String regexp;
    private String message;
    private String[] params;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                context.disableDefaultConstraintViolation();

                ValidationException exception = new ValidationException(this.message, this.params);
                try {
                    context.buildConstraintViolationWithTemplate(objectMapper.writeValueAsString(exception))
                            .addConstraintViolation();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        }

        return true;
    }
}
