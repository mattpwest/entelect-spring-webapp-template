package za.co.entelect.web.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;

import java.time.LocalDate;

@Category(UnitTest.class)
public class LocalDateFormatterTest {

    private LocalDateFormatter classUnderTest = new LocalDateFormatter();

    private static final String testDateString = "06/11/1983";
    private static final LocalDate testDate = LocalDate.of(1983,11,6);

    @Test
    public void testParse() throws Exception {
        LocalDate result = classUnderTest.parse(testDateString, null);

        Assert.assertEquals(testDate, result);
    }

    @Test
    public void testPrint() throws Exception {
        String result = classUnderTest.print(testDate, null);

        Assert.assertEquals(testDateString, result);
    }
}
