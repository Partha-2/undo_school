package com.undoschool.service;

import org.springframework.stereotype.Component;
import java.time.*;

@Component
public class TimezoneService {

    public ZoneId validate(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone);
        }
    }

    public OffsetDateTime localToUtc(String localDateTimeStr, String timezone) {
        ZoneId zone = validate(timezone);
        LocalDateTime local = LocalDateTime.parse(localDateTimeStr);
        ZonedDateTime zoned = ZonedDateTime.of(local, zone);
        return zoned.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);
    }

    public String utcToTimezoneWithOffset(OffsetDateTime utc, String timezone) {
        ZoneId zone = validate(timezone);
        ZonedDateTime zoned = utc.atZoneSameInstant(zone);
        return zoned.toOffsetDateTime().toString();
    }
}
