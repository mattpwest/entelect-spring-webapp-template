package za.co.entelect.services.validators.annotations;

import za.co.entelect.services.validators.FutureInclusiveValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FutureInclusiveValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureInclusive {
    String message() default "{validator.constraints.future.inclusive.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
