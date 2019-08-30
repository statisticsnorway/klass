package no.ssb.klass.initializer.stabas;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Can not migrate variants from stabas. However the stabas web service does not differentiate between versions and
 * variants. Hence all variants from stabas database is listed here so that the variants may be excluded.
 * <p>
 * The ids of the variants are found by the following sql query in the stabas database:
 * 
 * <pre>
 * select sysrid from classversion where varianttype != 0;
 * </pre>
 * <p>
 * Also some versions are actually meant to be variants, and are named something like "Variant 1 of ...". These are also
 * removed
 */
public final class VariantFilter {
    private static final List<String> STABAS_VARIANT_IDS = Lists.newArrayList("4684474", "4684730",
            "4684789", "4685048", "4685570", "522197", "4510001", "461552", "490909", "4360519", "486949", "491767",
            "493175", "511779", "508551", "474360", "467926", "2912116", "4220126", "480794", "492597", "497384",
            "502968", "4636757", "4637055", "4637290", "4637590", "4360185", "5538021", "5538039", "5536193", "5538075",
            "5538092", "5043068", "5042478", "5042742", "5042801", "5043334", "5564836",
            "5589336", "5589606", "5589883", "5589065", "5588754", "5588815", "8108486", "8108778", "8108839",
            "8109089", "8109360", "8109630");
    private static final List<String> STABAS_VERSIONS_THAT_ARE_VARIANTS_IDS = Lists.newArrayList("4220727", "4220707",
            "4220577", "4221584", "4221514", "4220740", "4221106", "5326001", "5326201");
    private static final List<String> ALL_VARIANT_IDS;

    static {
        ALL_VARIANT_IDS = Lists.newArrayList();
        ALL_VARIANT_IDS.addAll(STABAS_VARIANT_IDS);
        ALL_VARIANT_IDS.addAll(STABAS_VERSIONS_THAT_ARE_VARIANTS_IDS);
    }

    private VariantFilter() {
        // Utility class
    }

    public static boolean isVariant(String stabasVersionId) {
        return ALL_VARIANT_IDS.contains(stabasVersionId);
    }
}