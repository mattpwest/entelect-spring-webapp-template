package za.co.entelect.web.converters;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class LocalDateFormatter implements Formatter<LocalDate> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public LocalDate parse(String s, Locale locale) throws ParseException {
        return LocalDate.from(formatter.parse(s));
    }

    @Override
    public String print(LocalDate localDate, Locale locale) {
        return formatter.format(localDate);
    }
}
