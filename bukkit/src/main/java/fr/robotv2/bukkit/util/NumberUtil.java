package fr.robotv2.bukkit.util;

public class NumberUtil {

    private NumberUtil() { }

    public static boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
