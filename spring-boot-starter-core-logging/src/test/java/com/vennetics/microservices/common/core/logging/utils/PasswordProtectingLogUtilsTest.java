package com.vennetics.microservices.common.core.logging.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PasswordProtectingLogUtilsTest {

    @Test
    public void shouldHideProtectedParams() {
        PasswordProtectingLogUtils.getExcludedFieldNames().forEach(name -> {
            assertEquals("<*protected*>",
                         PasswordProtectingLogUtils.paramValueToString(name, "test"));

        });
    }

    @Test
    public void shouldNotHideUnprotectedParams() {
        assertEquals("test", PasswordProtectingLogUtils.paramValueToString("blah", "test"));
    }

    @Test
    public void shouldReturnNullForNullInput() {

        assertNull(PasswordProtectingLogUtils.excludeFieldNames(null));
        assertNull(PasswordProtectingLogUtils.paramValueToString("test", null));
        assertNull(PasswordProtectingLogUtils.passwordSensitiveObjectToString(null));

    }

    @Test
    public void shouldReturnToStringWhenImplemented() {

        assertEquals(new HardCodedToString().toString(),
                     PasswordProtectingLogUtils.passwordSensitiveObjectToString(new HardCodedToString()));

        assertEquals(new HardCodedToString().toString(),
                     PasswordProtectingLogUtils.paramValueToString("blah",
                                                                   new HardCodedToString()));

    }

    @Test
    public void shouldReturnStringsUnchanged() {

        assertEquals("Test", PasswordProtectingLogUtils.passwordSensitiveObjectToString("Test"));

    }

    @Test
    public void shouldExcludeFieldsInStringMatchingExcluded() {

        assertEquals("password=" + "<*protected*>",
                     PasswordProtectingLogUtils.excludeFieldNames("password=abc"));

    }

    @Test
    public void shouldFilterPasswordsWhenUsingReflection() {

        assertFalse(PasswordProtectingLogUtils.passwordSensitiveObjectToString(new NoToStringContainsPassword("passwordValue"))
                                              .contains("password="));

    }



    private static class HardCodedToString {

        @Override
        public String toString() {
            return "HardCodedToString";
        }
    }

    private static class NoToStringContainsPassword {

        private final String password;

        NoToStringContainsPassword(final String password) {
            this.password = password;
        }

        @SuppressWarnings("unused")
        public String getPassword() {
            return password;
        }
    }

}
