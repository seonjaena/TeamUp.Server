package com.sjna.teamup.common.controller.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjna.teamup.common.controller.constraint.SizeConstraint;
import com.sjna.teamup.common.domain.ValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class SizeValidator implements ConstraintValidator<SizeConstraint, String> {

    private String message;
    private String[] params;
    private int min;
    private int max;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        return true;
    }
}
