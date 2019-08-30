package no.ssb.klass.designer.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.designer.exceptions.ParameterException;

/**
 * @author Mads Lundemo, SSB.
 */
public final class ParameterUtil {
    private static final Logger log = LoggerFactory.getLogger(ParameterUtil.class);
    // Note: The World Wide Web Consortium Recommendation states that UTF-8 should be used. Not doing so may introduce
    // incompatibilities.
    private static final String DEFAULT_CHARSETS = StandardCharsets.UTF_8.toString();
    private static final String DEFAULT_SEPARATOR = "&";
    public static final String DEFAULT_DATE_MASK = "yyyy-MM-dd";

    private ParameterUtil() {
    }

    public static DateRange getRequiredDataRange(String valdiFromKey, String validToKey, String parametersString) {
        DateRange dateRange = null;
        DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_MASK);
        Map<String, String> parameters = ParameterUtil.decodeParameters(parametersString);
        dateRange = DateRange.create(createFromDate(defaultDateFormatter, parameters.get(valdiFromKey)),
                createToDate(defaultDateFormatter, parameters.get(validToKey)));
        return dateRange;
    }

    public static DateRange getOptionalDataRange(String valdiFromKey, String validToKey, String parametersString) {
        DateRange dateRange = null;
        DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_MASK);
        Map<String, String> parameters = ParameterUtil.decodeParameters(parametersString);
        String fromString = parameters.get(valdiFromKey);
        String toString = parameters.get(validToKey);
        if (Objects.equals(fromString, "null") && Objects.equals(toString, "null")) {
            return DateRange.create((LocalDate) null, null);
        }
        dateRange = DateRange.create(createFromDate(defaultDateFormatter, fromString),
                createToDate(defaultDateFormatter, toString));
        return dateRange;
    }

    public static String getRequiredStringParameter(String paramName, String parametersString) {
        Map<String, String> parameters;
        try {
            parameters = ParameterUtil.decodeParameters(parametersString);
        } catch (Exception e) {
            throw new ParameterException(paramName, parametersString);
        }
        if (!parameters.containsKey(paramName)) {
            throw new ParameterException("Mangler parameter", paramName);
        } else {
            return parameters.get(paramName);
        }
    }

    public static Integer getRequiredIntParameter(String paramName, String parametersString) {
        Map<String, String> parameters;
        try {
            parameters = ParameterUtil.decodeParameters(parametersString);
        } catch (Exception e) {
            throw new ParameterException(paramName, parametersString, e);
        }
        if (!parameters.containsKey(paramName)) {
            return 0;
        } else {
            return paresInt(paramName, parameters.get(paramName));
        }
    }

    public static Long getRequiredLongParameter(String paramName, String parametersString) {
        String versionIdString = null;
        try {
            Map<String, String> parameters = decodeParameters(parametersString);
            versionIdString = parameters.get(paramName);

        } catch (Exception e) {
            throw new ParameterException(paramName, parametersString, e);
        }
        if (versionIdString == null) {
            throw new ParameterException(paramName, parametersString);
        }
        return parseId(paramName, versionIdString);
    }

    public static boolean hasParameter(String paramName, String parametersString) {
        String versionIdString = null;
        try {
            Map<String, String> parameters = decodeParameters(parametersString);
            versionIdString = parameters.get(paramName);
        } catch (Exception e) {
            throw new ParameterException(paramName, parametersString, e);
        }
        return versionIdString != null;
    }

    private static Long parseId(String parameterName, String idString) {
        Long id = null;
        try {
            id = Long.parseLong(idString);
            return id;
        } catch (NumberFormatException e) {
            throw new ParameterException(parameterName, idString, e);
        }
    }

    private static Integer paresInt(String parameterName, String field) {
        Integer value = null;
        try {
            value = Integer.parseInt(field);
            return value;
        } catch (NumberFormatException e) {
            throw new ParameterException(parameterName, field, e);
        }
    }

    public static String encodeParameters(Map<String, String> parameters) {
        StringBuilder parameterBuilder = new StringBuilder();
        String separator = "?";
        String value;
        String name;
        for (Map.Entry<String, String> values : parameters.entrySet()) {
            try {
                if (parameterBuilder.length() != 0) {
                    separator = DEFAULT_SEPARATOR;
                }
                name = URLEncoder.encode(values.getKey(), DEFAULT_CHARSETS);
                value = URLEncoder.encode(values.getValue(), DEFAULT_CHARSETS);
                parameterBuilder
                        .append(separator)
                        .append(name)
                        .append("=")
                        .append(value);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unable to encode parameters, unsupported charsets set:" + DEFAULT_CHARSETS,
                        e);
            }
        }
        return parameterBuilder.toString();
    }

    public static Map<String, String> decodeParameters(String parametersString) {
        String[] parameter;
        String value;
        String name;
        Map<String, String> parameters = new HashMap<>();
        if (Strings.isNullOrEmpty(parametersString)) {
            return parameters;
        }
        if (parametersString.charAt(0) == '?') {
            parametersString = parametersString.substring(1);
        }
        if (StringUtils.isEmpty(parametersString)) {
            return parameters;
        }

        String[] encodedParams = parametersString.split(DEFAULT_SEPARATOR);
        for (String param : encodedParams) {
            try {
                parameter = param.split("=");
                name = URLDecoder.decode(parameter[0], DEFAULT_CHARSETS);
                value = URLDecoder.decode(parameter[1], DEFAULT_CHARSETS);
                parameters.put(name, value);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unable to decode parameters, unsupported charsets set:" + DEFAULT_CHARSETS,
                        e);
            } catch (Exception ex) {
                throw new ParameterException("Parametre:", parametersString);
            }
        }
        return parameters;
    }

    private static LocalDate createFromDate(DateTimeFormatter defaultDateFormatter, String fromString) {
        try {
            return LocalDate.parse(fromString, defaultDateFormatter);
        } catch (DateTimeParseException e) {
            log.warn("Feilet med å parse dato: " + fromString + " Feilmelding: " + e.getMessage());
            throw new ParameterException("Fra dato", fromString, e);
        }
    }

    private static LocalDate createToDate(DateTimeFormatter defaultDateFormatter, String toString) {
        LocalDate to = null;
        // TODO find a better way to handle max dates from parameters
        if (LocalDate.MAX.toString().equals(toString)) {
            return LocalDate.MAX;
        }
        if (toString != null && !toString.equals("null")) {
            try {
                to = VaadinUtil.convertToExclusive(LocalDate.parse(toString, defaultDateFormatter));
            } catch (DateTimeParseException e) {
                log.warn("Feilet med å parse dato: " + toString + " Feilmelding: " + e.getMessage());
                throw new ParameterException("Til dato", toString, e);
            }
        }
        return to;
    }
}
