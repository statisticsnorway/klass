package no.ssb.klass.designer.exceptions;

import com.google.common.base.Strings;

@SuppressWarnings("serial")
public class ParameterException extends KlassRuntimeException {
    String context;
    String parameterName;
    String parameterString;

    public ParameterException(String parameterName, String parameterString) {
        this.parameterName = parameterName;
        setParameter(parameterString);
    }

    public ParameterException(String parameterName, String parameterString, Exception ex) {
        super(ex);
        this.parameterName = parameterName;
        setParameter(parameterString);
    }

    public ParameterException(String context, String parameterName, String parameterString) {
        this.context = context;
        this.parameterName = parameterName;
        setParameter(parameterString);
    }

    public String getContext() {
        return context;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterString() {
        return parameterString;
    }

    private void setParameter(String value) {
        if (!Strings.isNullOrEmpty(value)) {
            if (value.charAt(0) == '?') {
                parameterString = value.substring(1);
            } else {
                parameterString = parameterName + '=' + value;
            }
        }
    }
}
