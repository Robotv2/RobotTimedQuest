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

        final long[] extraction = extract(service);

        if(extraction[0] == 0) {
            return String.format("%dh%dmin(s)", extraction[1], extraction[2]);
        } else {
            return String.format("%dd%dh%dmin(s)", extraction[0], extraction[1], extraction[2]);
        }
    }

    public static long[] extract(ResetService service) {
        final long[] extraction = new long[3];

        final Duration duration = Duration.ofMillis(DateUtil.getTimeUntil(service));
        final long days = duration.toDays();
        final long hours = duration.minusDays(days).toHours();
        final long minutes = duration.minusHours(hours).toMinutes();

        extraction[0] = days;
        extraction[1] = hours;
        extraction[2] = minutes;

        return extraction;
    }
}
