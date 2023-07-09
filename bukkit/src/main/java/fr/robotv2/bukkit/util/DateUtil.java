package fr.robotv2.bukkit.util;

import fr.robotv2.common.reset.ResetService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class DateUtil {

    // UTILITY CLASS

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static String getDateFormatted(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String getDateFormatted(long timestamp) {
        return DateUtil.getDateFormatted(new Date(timestamp));
    }

    public static String getDateFormatted(ResetService service) {
        return DateUtil.getDateFormatted(service.getNextExecution());
    }

    public static long getTimeUntil(ResetService service) {
        return service.getNextExecution() - System.currentTimeMillis();
    }

    public static String getTimeUntilFormatted(ResetService service) {
        final Duration duration = Duration.ofMillis(DateUtil.getTimeUntil(service));

        final long days = duration.toDays();
        final long hours = duration.minusDays(days).toHours();
        final long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

        if(days == 0) {
            return String.format("%dh%dmin(s)", hours, minutes);
        } else {
            return String.format("%dd%dh%dmin(s)", days, hours, minutes);
        }
    }
}
