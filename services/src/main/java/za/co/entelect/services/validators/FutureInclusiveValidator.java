package za.co.entelect.services.validators;

import org.apache.commons.lang3.time.DateUtils;
import za.co.entelect.services.validators.annotations.FutureInclusive;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class FutureInclusiveValidator implements ConstraintValidator<FutureInclusive,Date> {

    @Override
    public void initialize(FutureInclusive constraintAnnotation) {

    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        Date today = Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(2)));

        return DateUtils.isSameDay(value, today) || value.after(today);
    }
}
