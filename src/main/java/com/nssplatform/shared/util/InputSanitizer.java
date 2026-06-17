package com.nssplatform.shared.util;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class InputSanitizer {

    private InputSanitizer() {
        // Prevent instantiation
    }

    public static String sanitize(@NotNull @Size(max = 255) String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return Jsoup.clean(input.trim(), Safelist.none());
    }

    public static String sanitize(@NotNull @Size(max = 255) String input, int maxLength) {
        if (maxLength < 1) {
            throw new IllegalArgumentException("Max length must be a positive integer");
        }
        String cleaned = sanitize(input);
        if (cleaned == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        return cleaned.length() > maxLength ? cleaned.substring(0, maxLength) : cleaned;
    }
}