package com.vennetics.microservices.common.core.logging.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for avoiding the debugging of password information.
 */
public final class PasswordProtectingLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(PasswordProtectingLogUtils.class);

    private static final String PROTECTED = "<*protected*>";

    /**
     * List of field names that will not be logged when encountered
     */
    private static final Collection<String> EXCLUDED_FIELD_NAMES = new ArrayList<String>();

    static {
        EXCLUDED_FIELD_NAMES.add("password");
        EXCLUDED_FIELD_NAMES.add("j_password");
        EXCLUDED_FIELD_NAMES.add("Password");
        EXCLUDED_FIELD_NAMES.add("newPassword");
        EXCLUDED_FIELD_NAMES.add("secret");
        EXCLUDED_FIELD_NAMES.add("client_secret");
    }


    private PasswordProtectingLogUtils() {

    }

    public static Collection<String> getExcludedFieldNames() {
        return EXCLUDED_FIELD_NAMES;
    }

    /**
     * Checks param name and if it matches excluded fields will redact value
     *
     * @param paramName
     * @param paramValue
     * @return unredacted if paramName is not protected. A redacted value if it
     *         is.
     */
    public static String paramValueToString(final String paramName, final Object paramValue) {

        if (PasswordProtectingLogUtils.EXCLUDED_FIELD_NAMES.contains(paramName)) {
            return PROTECTED;
        }

        // Since value is a POJO it still needs checked
        return passwordSensitiveObjectToString(paramValue);
    }

    /**
     * Calls toString on an object by applying rules.
     *
     * Firstly checks if it is a JPA entity or implements toString(). If so it
     * will use the classes toString. Else it will apply redacting rules.
     *
     * @param value
     * @return the object as a string. If the object contains suspected password
     *         info it will be protected.
     */
    public static String passwordSensitiveObjectToString(final Object value) {

        String result;
        if (value == null) {

            result = null;

        } else if (!(value instanceof String) && isToStringImplemented(value)) {

            // For classes with explicit string implementations use it
            // Assume developer had the sense not to log a password.

            result = value.toString();

        } else {

            String unformattedString;
            if (value instanceof String) {

                unformattedString = value.toString();
            } else {

                unformattedString = reflectionToString(value);
            }

            // Belt and braces in case string contains of the form password=blah
            result = PasswordProtectingLogUtils.excludeFieldNames(unformattedString);
        }
        return result;
    }

    /**
     * If the string contains an excluded field name then its content is
     * redacted. Useful for a whole URI path with parameters.
     *
     * @param content
     * @return the formatted string
     */
    public static String excludeFieldNames(final String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        String result = content;
        for (final String excludedName : PasswordProtectingLogUtils.EXCLUDED_FIELD_NAMES) {
            final String pattern = excludedName + "=[^&]*";
            final Pattern r = Pattern.compile(pattern);
            final Matcher m = r.matcher(result);
            if (m.find()) {
                result = m.replaceAll(excludedName + "=" + PROTECTED);
            }
        }

        return result;
    }

    /**
     * @param object
     * @return true if the object declaring class implements toString()
     */
    private static boolean isToStringImplemented(final Object object) {
        try {
            return !object.getClass()
                          .getMethod("toString")
                          .getDeclaringClass()
                          .equals(Object.class);
        } catch (NoSuchMethodException | SecurityException e) {
            // Not a signinficant problem if toString not implemented
            logger.trace("ToString not found on {}", object.getClass(), e);
            return false;
        }
    }

    private static String reflectionToString(final Object pojo) {
        return ReflectionToStringBuilder.toStringExclude(pojo, EXCLUDED_FIELD_NAMES);
    }
}
