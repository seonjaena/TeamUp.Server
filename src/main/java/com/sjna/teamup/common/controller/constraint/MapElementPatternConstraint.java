package com.sjna.teamup.common.controller.constraint;

import com.sjna.teamup.common.controller.validator.MapElementPatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MapElementPatternValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MapElementPatternConstraint {
    String regexp() default "";
    String message() default "";
    String[] params() default {};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
