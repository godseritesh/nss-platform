package com.nssplatform.shared.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class InputSanitizer {

    private static final Safelist SAFELIST = Safelist.none();

    public static String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input.trim(), SAFELIST);
    }

    public static String sanitize(String input, int maxLength) {
        String cleaned = sanitize(input);
        if (cleaned == null) return null;
        return cleaned.length() > maxLength ? cleaned.substring(0, maxLength) : cleaned;
    }
}
