package no.ssb.klass.forvaltning.converting.xml.fullversionexport;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import no.ssb.klass.forvaltning.converting.xml.dto.XmlCorrespondenceContainer;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVariantContainer;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionContainer;

/**
 * @author Mads Lundemo, SSB.
 *
 *         Util class provides column names for full xml export of version.
 */
final class FullExportColumnUtil {

    private FullExportColumnUtil() {
    }

    private static final List<String> VERSION_MINIMUM_COLUMNS = ImmutableList.of(
            XmlVersionContainer.CODE, XmlVersionContainer.PARENT,
            XmlVersionContainer.NAME_NB, XmlVersionContainer.NAME_NN, XmlVersionContainer.NAME_EN);

    private static final List<String> VERSION_SHORT_NAME_COLUMNS = ImmutableList.of(
            XmlVersionContainer.SHORT_NAME_NB, XmlVersionContainer.SHORT_NAME_NN, XmlVersionContainer.SHORT_NAME_EN);

    private static final List<String> VERSION_NOTES_COLUMNS = ImmutableList.of(
            XmlVersionContainer.NOTES_NB, XmlVersionContainer.NOTES_NN, XmlVersionContainer.NOTES_EN);

    /** NOTE: dropping KILDE_KODE and KILDE_TITTEL as version got this already */
    private static final List<String> CORRESPONDENCE_TABLE_COLUMNS = ImmutableList.of(
            XmlCorrespondenceContainer.MAAL_KODE, XmlCorrespondenceContainer.MAAL_TITTEL);

    private static final List<String> VARIANT_COLUMNS = ImmutableList.of(
            XmlVariantContainer.KODE, XmlVariantContainer.NAVN_BOKMAAL,
            XmlVariantContainer.NAVN_NYNORSK, XmlVariantContainer.NAVN_ENGELSK,
            XmlVariantContainer.KILDE_KODE, XmlVariantContainer.FORELDER);

    static List<String> getVariantColumnsInExportOrder() {
        return VARIANT_COLUMNS;
    }

    static List<String> getCorrespondenceColumnsInExportOrder() {
        return CORRESPONDENCE_TABLE_COLUMNS;
    }

    static List<String> getVersionColumnsInExportOrder(boolean shortNames, boolean notes) {
        List<String> columns = new LinkedList<>();
        columns.addAll(VERSION_MINIMUM_COLUMNS);
        if (shortNames) {
            columns.addAll(FullExportColumnUtil.VERSION_SHORT_NAME_COLUMNS);
        }
        if (notes) {
            columns.addAll(FullExportColumnUtil.VERSION_NOTES_COLUMNS);
        }
        return columns;
    }
}
