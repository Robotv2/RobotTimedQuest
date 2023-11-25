package fr.robotv2.common.util;

public class OrmUtil {
    public static String convertToPythonString(String javaString) {
        StringBuilder pythonString = new StringBuilder();

        for (int i = 0; i < javaString.length(); i++) {
            char currentChar = javaString.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                if (i > 0) {
                    pythonString.append('_'); // Add underscore before uppercase letters
                }
                pythonString.append(Character.toLowerCase(currentChar));
            } else {
                pythonString.append(currentChar);
            }
        }

        return pythonString.toString();
    }
}
