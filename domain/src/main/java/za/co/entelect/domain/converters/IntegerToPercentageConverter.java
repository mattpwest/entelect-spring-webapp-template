package za.co.entelect.domain.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class IntegerToPercentageConverter implements AttributeConverter<Double, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Double value) {
        return (int) (value * 100);
    }

    @Override
    public Double convertToEntityAttribute(Integer value) {
        return ((double) value)/100.00;
    }
}
