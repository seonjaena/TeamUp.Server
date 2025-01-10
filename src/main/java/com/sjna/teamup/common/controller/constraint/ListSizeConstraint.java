package com.sjna.teamup.common.controller.constraint;

import com.sjna.teamup.common.controller.validator.ListSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ListSizeValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ListSizeConstraint {
    String message() default "";
    String[] params() default {};
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
