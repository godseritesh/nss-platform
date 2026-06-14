package com.nssplatform.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InputSanitizerTest {

    @Test
    void removesHtmlTags() {
        assertThat(InputSanitizer.sanitize("<script>alert('xss')</script>Hello"))
            .isEqualTo("Hello");
    }

    @Test
    void returnsNullForNullInput() {
        assertThat(InputSanitizer.sanitize(null)).isNull();
    }

    @Test
    void trimsWhitespace() {
        assertThat(InputSanitizer.sanitize("  hello  ")).isEqualTo("hello");
    }

    @Test
    void allowsPlainText() {
        assertThat(InputSanitizer.sanitize("John Doe")).isEqualTo("John Doe");
    }

    @Test
    void respectsMaxLength() {
        String input = "a".repeat(200);
        assertThat(InputSanitizer.sanitize(input, 50)).hasSize(50);
    }

    @Test
    void stripsAttributes() {
        assertThat(InputSanitizer.sanitize("<a href=\"http://evil.com\">click</a>"))
            .isEqualTo("click");
    }
}
