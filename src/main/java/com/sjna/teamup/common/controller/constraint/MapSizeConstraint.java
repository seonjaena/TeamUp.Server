package com.sjna.teamup.common.controller.constraint;

import com.sjna.teamup.common.controller.validator.MapSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MapSizeValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MapSizeConstraint {
    String message() default "";
    String[] params() default {};
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
