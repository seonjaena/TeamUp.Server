package com.sjna.teamup.validator.constraint;

import com.sjna.teamup.validator.validator.MapElementPatternValidator;
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
