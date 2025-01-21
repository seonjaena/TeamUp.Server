package com.sjna.teamup.common.controller.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjna.teamup.common.domain.VALID_REGEX;
import com.sjna.teamup.common.controller.constraint.UserNicknameConstraint;
import com.sjna.teamup.common.domain.ValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class UserNicknameValidator implements ConstraintValidator<UserNicknameConstraint, String> {
    private String message;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void initialize(UserNicknameConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = StringUtils.isBlank(constraintAnnotation.message()) ? "constraint.user-nickname.pattern" : constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String userNickname, ConstraintValidatorContext context) {
        if(userNickname == null || !userNickname.matches(VALID_REGEX.USER_NICKNAME.getRegexp())) {
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
