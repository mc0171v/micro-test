package com.vennetics.microservices.common.core.logging.utils;

import static com.vennetics.microservices.common.core.logging.LoggingConstants.INDENT;
import static com.vennetics.microservices.common.core.logging.LoggingConstants.NEW_LINE;

/**
 * Core logging utils.
 */
public final class CoreLogUtils {

    private CoreLogUtils() {

    }

    /**
     * Append a key value to a log builder with a configurable indent.
     *
     * @param builder
     * @param key
     * @param value
     * @param indentLevel
     */
    public static void appendKeyValue(final StringBuilder builder,
                                      final String key,
                                      final Object value,
                                      final int indentLevel) {

        for (int i = 0; i < indentLevel; i++) {
            builder.append(INDENT);
        }

        builder.append(key);
        builder.append("=[");
        builder.append(PasswordProtectingLogUtils.paramValueToString(key, value));
        builder.append(']');

        builder.append(NEW_LINE);
    }

    /**
     * Used in PJP logging to log parameters
     *
     * @param builder
     * @param names
     * @param arguments
     * @param indentLevel
     */
    public static void appendNamedParameters(final StringBuilder builder,
                                             final String[] names,
                                             final Object[] arguments,
                                             final int indentLevel) {

        if (arguments != null) {

            for (int i = 0; i < arguments.length; i++) {

                // Null check in case param name could not be determined e.g.
                // via reflection
                final String key = names != null ? names[i] : "arg" + (i + 1);

                final Object value = arguments[i];

                appendKeyValue(builder, key, value, indentLevel);

            }
        }
    }

    /**
     * Map a PJP return value to a string. Checks for void results.
     *
     * @param returnValue
     * @return the string value
     */
    public static String returnValueAsString(final Object returnValue) {

        final String returnAsString;
        if (returnValue != null) {
            if (returnValue.getClass().isPrimitive()) {
                returnAsString = "<" + returnValue + ">";
            } else {
                returnAsString = "<"
                                + PasswordProtectingLogUtils.passwordSensitiveObjectToString(returnValue)
                                + ">";
            }
        } else {
            returnAsString = "<void>";
        }
        return returnAsString;
    }
}
