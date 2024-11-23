package com.universityweb.common;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class Utils {
    public static final BigDecimal MIN_PRICE_LIMIT = new BigDecimal(10_000);

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Converts a string representing milliseconds since epoch to a LocalDateTime.
     *
     * @param millisStr the string representing milliseconds since epoch
     * @return LocalDateTime object corresponding to the given milliseconds
     * @throws IllegalArgumentException if the input string is not a valid long
     */
    public static LocalDateTime convertMillisToLocalDateTime(String millisStr) {
        if (millisStr == null || millisStr.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        try {
            long millis = Long.parseLong(millisStr);
            Instant instant = Instant.ofEpochMilli(millis);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input: " + millisStr, e);
        }
    }

    public static Boolean isEquals(List<String> list1, List<String> list2) {
        if (list1 == null) {
            return list2 == null;
        }

        if (list2 == null) {
            return false;
        }

        int size1 = list1.size();
        int size2 = list2.size();

        // If the sizes are different, the lists cannot be equal
        if (size1 != size2) {
            return false;
        }

        // Iterate through both lists and compare each string
        for (int i = 0; i < size1; i++) {
            String str1 = list1.get(i);
            String str2 = list2.get(i);

            // Compare word by word for each line if they are equal
            String[] words1 = str1.split("\\s+");
            String[] words2 = str2.split("\\s+");

            // Check if the word counts in the strings are the same
            if (words1.length != words2.length) {
                return false;
            }

            // Compare words at each position
            for (int j = 0; j < words1.length; j++) {
                if (!words1[j].equals(words2[j])) {
                    return false;
                }
            }
        }

        // If all lines and words match, the lists are equal
        return true;
    }

    public static  <E extends Enum<E>> E safeEnumConversion(Class<E> enumClass, String value) {
        if (value == null || value.isBlank()) {
            return null; // Return null or a default value if needed
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Return null or a fallback value
        }
    }
}
