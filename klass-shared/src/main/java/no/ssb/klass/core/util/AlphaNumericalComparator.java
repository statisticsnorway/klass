package no.ssb.klass.core.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class AlphaNumericalComparator implements Comparator {
    private final boolean reverseNumberOrder;

    public AlphaNumericalComparator() {
        this(false);
    }

    private AlphaNumericalComparator(boolean reverseNumberOrder) {
        this.reverseNumberOrder = reverseNumberOrder;
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor, boolean ReverseNumberOrder) {
        Objects.requireNonNull(keyExtractor);
        AlphaNumericalComparator comparator = new AlphaNumericalComparator(ReverseNumberOrder);
        return (Comparator<T> & Serializable)
                (c1, c2) -> comparator.compare(keyExtractor.apply(c1), keyExtractor.apply(c2));
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        AlphaNumericalComparator comparator = new AlphaNumericalComparator();
        return (Comparator<T> & Serializable)
                (c1, c2) -> comparator.compare(keyExtractor.apply(c1), keyExtractor.apply(c2));
    }

    private boolean isNumber(char character) {
        return character >= 48 && character <= 57;
    }

    private String getBlock(String string, int length, int pos) {
        char firstChar = string.charAt(pos++);
        return isNumber(firstChar)
                ? getNumberBlock(string, length, pos, firstChar)
                : getTextBlock(string, length, pos, firstChar);
    }

    private String getTextBlock(String string, int length, int pos, char character) {
        StringBuilder block = new StringBuilder();
        block.append(character);
        while (pos < length) {
            character = string.charAt(pos);
            if (isNumber(character)) {
                return block.toString();
            } else {
                block.append(character);
                ++pos;
            }
        }
        return block.toString();
    }

    private String getNumberBlock(String string, int length, int pos, char character) {
        StringBuilder block = new StringBuilder();
        block.append(character);

        while (pos < length) {
            character = string.charAt(pos);
            if (!isNumber(character)) {
                return block.toString();
            } else {
                block.append(character);
                ++pos;
            }
        }
        return block.toString();
    }

    public int compare(Object o1, Object o2) {
        if (!(o1 instanceof String) || !(o2 instanceof String)) {
            return 0;
        }
        String text1 = (String) o1;
        String text2 = (String) o2;

        int result, pos1 = 0, pos2 = 0;
        int length1 = text1.length();
        int length2 = text2.length();

        while (pos1 < length1 && pos2 < length2) {
            String block1 = getBlock(text1, length1, pos1);
            String block2 = getBlock(text2, length2, pos2);

            pos1 += block1.length();
            pos2 += block2.length();

            if (isNumber(block1.charAt(0)) && isNumber(block2.charAt(0))) {
                // compare numbers
                int blockSize1 = block1.length();
                result = blockSize1 - block2.length();
                // if equal length compare, else the longest one is highest number
                // TODO : how should we handle numbers with leading 0s? ex: 0010 vs 11
                if (result == 0) {
                    for (int i = 0; i < blockSize1; i++) {
                        result = block1.charAt(i) - block2.charAt(i);
                        if (result != 0) {
                            return reverseNumberOrder ? -result : result;
                        }
                    }
                } else {
                    result = reverseNumberOrder ? -result : result;
                }
            } else {
                // normal string compare
                result = block1.compareTo(block2);
            }

            if (result != 0) {
                return result;
            }
        }

        return length1 - length2;
    }
}
