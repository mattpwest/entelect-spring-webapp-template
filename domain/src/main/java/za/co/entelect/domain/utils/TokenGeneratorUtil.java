package za.co.entelect.domain.utils;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class TokenGeneratorUtil {

    public static final int TOKEN_LENGTH_IN_BYTES = 16;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken(LocalDateTime expiresAt) {
        Instant instant = expiresAt.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);
        Long expiryTimeMs = date.getTime();

        int numTimeBytes = Long.SIZE / Byte.SIZE;
        int numRandomBytes = TOKEN_LENGTH_IN_BYTES - numTimeBytes;
        byte[] tokenBytes = ByteBuffer.allocate(TOKEN_LENGTH_IN_BYTES)
            .putLong(expiryTimeMs)
            .put(secureRandom.generateSeed(numRandomBytes))
            .array();

        return DatatypeConverter.printHexBinary(tokenBytes);
    }
}
