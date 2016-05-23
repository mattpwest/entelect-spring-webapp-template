package za.co.entelect.domain.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import za.co.entelect.UnitTest;

import java.time.LocalDateTime;

@Category(UnitTest.class)
public class TokenGeneratorUtilTest {

    @Test
    public void testGenerateToken() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String token = TokenGeneratorUtil.generateToken(localDateTime);

        Assert.assertNotNull(token);
        Assert.assertFalse(token.isEmpty());
        Assert.assertEquals("Token string length should be twice the byte length, since it is converted to hex.",
            TokenGeneratorUtil.TOKEN_LENGTH_IN_BYTES * 2,token.length());
    }
}
