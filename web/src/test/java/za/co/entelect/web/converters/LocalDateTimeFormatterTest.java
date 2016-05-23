package za.co.entelect.web.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Category(UnitTest.class)
public class LocalDateTimeFormatterTest {

    private LocalDateTimeFormatter classUnderTest = new LocalDateTimeFormatter();

    private static final String testDateString = "06/11/1983 00:00:00";
    private static final LocalDateTime testDate = LocalDate.of(1983,11,6).atTime(0, 0);

    @Test
    public void testParse() throws Exception {
        LocalDateTime result = classUnderTest.parse(testDateString, null);

        Assert.assertEquals(testDate, result);
    }

    @Test
    public void testPrint() throws Exception {
        String result = classUnderTest.print(testDate, null);

        Assert.assertEquals(testDateString, result);
    }
}
