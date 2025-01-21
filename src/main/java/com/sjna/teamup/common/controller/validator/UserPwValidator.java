package com.sjna.teamup.common.controller.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjna.teamup.common.domain.VALID_REGEX;
import com.sjna.teamup.common.controller.constraint.UserPwConstraint;
import com.sjna.teamup.common.domain.ValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class UserPwValidator implements ConstraintValidator<UserPwConstraint, String> {

    private String message;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void initialize(UserPwConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "constraint.user-pw.pattern" : constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String userPw, ConstraintValidatorContext context) {
        if(userPw == null || !userPw.matches(VALID_REGEX.USER_PW.getRegexp())) {
            context.disableDefaultConstraintViolation();

            ValidationException exception = new ValidationException(this.message, null);
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
