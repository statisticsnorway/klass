package no.ssb.klass.auth;

import java.util.Arrays;
import java.util.List;

public class Claims {
    public static final String SHORT_USERNAME = "short_username";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String DAPLA = "dapla";
    public static final String SECTION_CODE = "section_code"; // Within the dapla claim

    public static final List<String> REQUIRED_CLAIMS = Arrays.asList(SHORT_USERNAME, EMAIL, NAME, DAPLA);
}
