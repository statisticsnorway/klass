package no.ssb.klass.api.converters;

import no.ssb.klass.api.dto.CodeList;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.testutil.ConstantClockSource;
import no.ssb.klass.testutil.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpOutputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CodeListCsvConverterTest {
    private CodeListCsvConverter subject;
    private Date now;

    @Before
    public void setup() {
        now = new Date();
        TimeUtil.setClockSource(new ConstantClockSource(now));
        subject = new CodeListCsvConverter();
    }

    @After
    public void teardown() {
        TimeUtil.revertClockSource();
    }

    @Test
    public void supports() {
        assertTrue(subject.supports(CodeList.class));
        assertFalse(subject.supports(CodeListCsvConverterTest.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readInternal() {
        subject.readInternal(null, null);
    }

    @Test
    public void writeInternalWithValidRange() throws Exception {
        // given
        final boolean showValidRange = true;
        final DateRange dateRange = null;
        HttpOutputMessage outputMessage = new MockHttpOutputMessage();

        // when
        subject.writeInternal(createCodeList(showValidRange, dateRange), outputMessage);

        // then
        assertEquals(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\","
                        + "\"validTo\",\"validFromInRequestedRange\",\"validToInRequestedRange\",\"notes\"\n"
                        + "\"0104\",,\"1\",\"Sandefjord\",\"\",\"\",,,\"2008-01-01\",\"2020-01-01\",\"\"\n",
                outputMessage.getBody().toString());
    }

    @Test
    public void writeInternalWithoutValidRange() throws Exception {
        // given
        final boolean showValidRange = false;
        final DateRange dateRange = null;
        HttpOutputMessage outputMessage = new MockHttpOutputMessage();

        // when
        subject.writeInternal(createCodeList(showValidRange, dateRange), outputMessage);

        // then
        assertEquals(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                        + "\"0104\",,\"1\",\"Sandefjord\",\"\",\"\",,,\"\"\n", outputMessage
                        .getBody()
                        .toString());
    }

    @Test
    public void writeInternalNotesWithQuotas() throws Exception {
        // given
        final boolean showValidRange = false;
        final DateRange dateRange = null;
        HttpOutputMessage outputMessage = new MockHttpOutputMessage();

        // when
        subject.writeInternal(createCodeListWithDoubleQuotedNote(showValidRange, dateRange), outputMessage);

        // then
        assertEquals(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                        + "\"0104\",,\"1\",\"Sandefjord\",\"\",\"\",,,\"Dette er en 'testnote' for Sandefjord  med ny linje\"\n", outputMessage
                        .getBody()
                        .toString());
    }


    private CodeList createCodeList(boolean withValidRange, DateRange dateRange) {
        CodeDto code = new CodeDto(TestUtil.createLevel(1), TestUtil.createClassificationItem("0104", "Sandefjord"),
                DateRange.create("2008-01-01", "2020-01-01"), Language.getDefault());
        return new CodeList(",", withValidRange, dateRange, null).convert(Arrays.asList(code));
    }

    private CodeList createCodeListWithDoubleQuotedNote(boolean withValidRange, DateRange dateRange) {
        CodeDto code = new CodeDto(TestUtil.createLevel(1),
                TestUtil.createClassificationItem("0104", "Sandefjord", "", "Dette er en \"testnote\" for Sandefjord \nmed ny linje"),
                DateRange.create("2008-01-01", "2020-01-01"), Language.getDefault());
        return new CodeList(",", withValidRange, dateRange, null).convert(Arrays.asList(code));
    }

}
