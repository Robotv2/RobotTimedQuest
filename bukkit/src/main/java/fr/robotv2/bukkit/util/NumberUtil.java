package fr.robotv2.bukkit.util;

import java.math.BigDecimal;

public class NumberUtil {

    // UTILITY CLASS

    private NumberUtil() { }

    public static Number toNumber(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    public static boolean isNumber(String value) {

        if(value == null) {
            return false;
        }

        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
