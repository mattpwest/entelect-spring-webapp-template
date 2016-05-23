package za.co.entelect.domain.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;
import za.co.entelect.domain.converters.LocalDateTimePersistenceConverter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Category(UnitTest.class)
public class LocalDateTimePersistenceConverterTest {

    private LocalDateTimePersistenceConverter converter = new LocalDateTimePersistenceConverter();

    @Test
    public void testConvertToDatabaseColumn() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp converted = converter.convertToDatabaseColumn(localDateTime);

        Assert.assertNotNull(converted);
    }

    @Test
    public void test() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        LocalDateTime converted = converter.convertToEntityAttribute(timestamp);

        Assert.assertNotNull(converted);
    }
}
