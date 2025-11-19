package no.ssb.klass.api.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.util.DateRange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class CodeListTest {

    /** Unit tests for filterOnCodes in CodeList. */
    @Nested
    class FilterOnCodes {

        private static CodeList makeCodeList(String... codes) {
            List<CodeDto> dtos = new ArrayList<>();
            Level level = new Level(1, null);
            Language language = Language.NB;
            DateRange dateRange = DateRange.create(LocalDate.MIN, LocalDate.MAX);
            for (String code : codes) {
                ClassificationItem item = new TestClassificationItem(code);
                dtos.add(new CodeDto(level, item, dateRange, language));
            }
            CodeList list = new CodeList(";", false, dateRange, false);
            return list.convert(dtos);
        }

        private CodeList baseList;
        private List<String> baseCodes;

        @BeforeEach
        void setUp() {
            baseList =
                    makeCodeList(
                            "1000", "1100", "1111", "1199", "1200", "3000", "3004", "3006", "3100",
                            "3200", "3399", "3400", "3410", "3499", "3500");
            baseCodes = baseList.getCodes().stream().map(CodeItem::getCode).toList();
        }

        @Nested
        @DisplayName("Happy path-cases")
        class SpecifiedCases {

            @Test
            void emptyString() {
                CodeList output = baseList.filterOnCodes("");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrderElementsOf(baseCodes);
                assertThat(output).isSameAs(baseList);
            }

            @ParameterizedTest
            @ValueSource(strings = {"    3004   ", " , , , 3499,  ", ",", "   ", "\t \n"})
            void whitespaceAndEmptyValues(String input) {
                CodeList output = baseList.filterOnCodes(input);
                if (input.contains("3004") || input.contains("3499")) {
                    assertThat(output.getCodes())
                            .extracting(CodeItem::getCode)
                            .containsAnyOf("3004", "3499");
                } else {
                    assertThat(output.getCodes())
                            .extracting(CodeItem::getCode)
                            .containsExactlyInAnyOrderElementsOf(baseCodes);
                }
            }

            @Test
            void nullString() {
                CodeList output = baseList.filterOnCodes(null);
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrderElementsOf(baseCodes);
                assertThat(output).isSameAs(baseList);
            }

            @Test
            void singleCode_exactMatch_3004() {
                CodeList output = baseList.filterOnCodes("3004");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrder("3004");
                assertThat(output).isNotSameAs(baseList);
            }

            @Test
            void multipleCodes_exactMatches_3004_and_3006() {
                CodeList output = baseList.filterOnCodes("3004, 3006");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrder("3004", "3006");
                assertThat(output).isNotSameAs(baseList);
            }

            @Test
            void codeAndWildcard_3004_and_11_star() {
                CodeList output = baseList.filterOnCodes("3004, 11*");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrder("3004", "1100", "1111", "1199");
                assertThat(output).isNotSameAs(baseList);
            }

            @Test
            void numericRange_inclusive_3004_to_3400() {
                CodeList output = baseList.filterOnCodes("3004-3400");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrder("3004", "3006", "3100", "3200", "3399", "3400");
                assertThat(output).isNotSameAs(baseList);
            }

            @Test
            void numericRangeAndWildcard() {
                CodeList output = baseList.filterOnCodes("3004-3400, 11*");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrder(
                                "3004", "3006", "3100", "3200", "3399", "3400", "1100", "1111",
                                "1199");
                assertThat(output).isNotSameAs(baseList);
            }

            @Test
            void wildcard_34_star() {
                CodeList output = baseList.filterOnCodes("34*");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrder("3400", "3410", "3499");
                assertThat(output).isNotSameAs(baseList);
            }

            @Test
            void numericRangeFromSingleCodeToWildcard() {
                CodeList output = baseList.filterOnCodes("3100-34*");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrder("3100", "3200", "3399", "3400", "3410", "3499");
                assertThat(output).isNotSameAs(baseList);
            }
        }

        @Nested
        @DisplayName("Validation")
        class Validation {
            @Test
            void allowedCharacters() {
                String input = "3004, 3006-3400, 11*,  1 , 2-3,    9*,\t 8-9";
                CodeList output = baseList.filterOnCodes(input);
                assertThat(output).isNotNull();
            }

            @ParameterizedTest
            @ValueSource(
                    strings = {
                        "a", "bcd", "2001.", "2002/", "2003\\", "2004:", "2005;", "2006|", "2007+",
                        "2 O O 8", "2009😆", "3001?", "3002=", "3003&", "3004%", "3005!", "3006@"
                    })
            void illegalCharacters(String invalidCode) {
                assertThatThrownBy(() -> baseList.filterOnCodes(invalidCode))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("illegal characters");
            }

            @Test
            void lengthIsMax() {
                String input = "1".repeat(200);
                CodeList output = baseList.filterOnCodes(input);
                assertThat(output).isNotNull();
            }

            @Test
            void lengthOffByOne() {
                String input = "1".repeat(201);
                assertThatThrownBy(() -> baseList.filterOnCodes(input))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("too long");
            }

            @Test
            void tooManyParameters() {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 21; i++) {
                    if (i > 0) sb.append(",");
                    sb.append("1001");
                }
                assertThatThrownBy(() -> baseList.filterOnCodes((sb.toString())))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Too many selectCodes parameters");
            }

            @Test
            void invalidRanges() {
                CodeList output = baseList.filterOnCodes("3004- , -3004, *-3004, 3004-*, *-*");
                assertThat(output.getCodes())
                        .extracting(CodeItem::getCode)
                        .containsExactlyInAnyOrderElementsOf(baseCodes);
            }
        }
    }

    /** Unit tests for combineCodeItems in CodeList. */
    @Nested
    class CombineCodeItems {

        @SuppressWarnings("SameParameterValue")
        private CodeItem.RangedCodeItem createItem(String code, LocalDate from, LocalDate to) {
            Level level = new Level(1, null);
            ClassificationItem item = new TestClassificationItem(code, from, to);
            DateRange range = DateRange.create(from, to);
            CodeDto dto = new CodeDto(level, item, range, Language.NB);
            return new CodeItem.RangedCodeItem(dto);
        }

        private CodeList newCodeListIncludeFutureTrue() {
            return new CodeList(";", false, null, true);
        }

        @Test
        void merges_back_to_back_intervals_into_one() {
            CodeList codeList = newCodeListIncludeFutureTrue();
            var item1 = createItem("3004", LocalDate.of(2000, 1, 1), LocalDate.of(2005, 1, 1));
            var item2 = createItem("3004", LocalDate.of(2005, 1, 1), LocalDate.of(2010, 1, 1));
            var result = codeList.combineCodeItems(item1, List.of(item1, item2));
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getValidFromInRequestedRange())
                    .isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(result.get(0).getValidToInRequestedRange())
                    .isEqualTo(LocalDate.of(2010, 1, 1));
        }

        @Test
        void does_not_merge_when_there_is_a_gap_between_intervals() {
            CodeList codeList = newCodeListIncludeFutureTrue();
            var item1 = createItem("3004", LocalDate.of(2000, 1, 1), LocalDate.of(2005, 1, 1));
            var item2 = createItem("3004", LocalDate.of(2006, 1, 1), LocalDate.of(2010, 1, 1));
            var result = codeList.combineCodeItems(item1, List.of(item1, item2));
            assertThat(result).hasSize(2);
        }

        @Test
        void sorts_input_before_merging() {
            CodeList codeList = newCodeListIncludeFutureTrue();
            var itemA = createItem("3004", LocalDate.of(2005, 1, 1), LocalDate.of(2010, 1, 1));
            var itemB = createItem("3004", LocalDate.of(2000, 1, 1), LocalDate.of(2005, 1, 1));
            var result = codeList.combineCodeItems(itemA, List.of(itemA, itemB));
            assertThat(result).hasSize(1);
        }

        @Test
        void merges_chain_but_keeps_last_isolated_interval() {
            CodeList codeList = newCodeListIncludeFutureTrue();
            var item1 = createItem("3004", LocalDate.of(2000, 1, 1), LocalDate.of(2005, 1, 1));
            var item2 = createItem("3004", LocalDate.of(2005, 1, 1), LocalDate.of(2010, 1, 1));
            var item3 = createItem("3004", LocalDate.of(2012, 1, 1), LocalDate.of(2015, 1, 1));
            var result = codeList.combineCodeItems(item1, List.of(item1, item2, item3));
            assertThat(result).hasSize(2);
        }
    }

    static class TestClassificationItem extends ClassificationItem {
        private final String code;
        private final LocalDate from;
        private final LocalDate to;

        TestClassificationItem(String code) {
            this(code, null, null);
        }

        TestClassificationItem(String code, LocalDate from, LocalDate to) {
            this.code = code;
            this.from = from;
            this.to = to;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public ClassificationItem getParent() {
            return null;
        }

        @Override
        public String getOfficialName(Language language) {
            return "Official " + code;
        }

        @Override
        public String getShortName(Language language) {
            return "Short " + code;
        }

        @Override
        public LocalDate getValidFrom() {
            return from;
        }

        @Override
        public LocalDate getValidTo() {
            return to;
        }

        @Override
        public ClassificationItem copy() {
            return this;
        }

        @Override
        public boolean isReference() {
            return false;
        }

        @Override
        public boolean hasNotes() {
            return false;
        }

        @Override
        public boolean hasShortName() {
            return false;
        }

        @Override
        public String getNotes(Language language) {
            return "";
        }
    }
}
