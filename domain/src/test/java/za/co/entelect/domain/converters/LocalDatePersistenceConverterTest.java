package za.co.entelect.domain.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;
import za.co.entelect.domain.converters.LocalDatePersistenceConverter;

import java.sql.Date;
import java.time.LocalDate;

@Category(UnitTest.class)
public class LocalDatePersistenceConverterTest {

    private LocalDatePersistenceConverter converter = new LocalDatePersistenceConverter();

    @Test
    public void testConvertToDatabaseColumn() {
        LocalDate localDate = LocalDate.now();
        Date converted = converter.convertToDatabaseColumn(localDate);

        Assert.assertNotNull(converted);
    }

    @Test
    public void test() {
        Date date = Date.valueOf(LocalDate.now());
        LocalDate converted = converter.convertToEntityAttribute(date);

        Assert.assertNotNull(converted);
    }
}

