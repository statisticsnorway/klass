package no.ssb.klass.core.util;

/**
 * @author Mads Lundemo, SSB.
 */
public final class AlphaNumericCompareUtil {

  private AlphaNumericCompareUtil() {}

  private static final AlphaNumericalComparator ALPHANUM_INSTANCE = new AlphaNumericalComparator();

  public static int compare(String a, String b) {
    return ALPHANUM_INSTANCE.compare(a, b);
  }
}
