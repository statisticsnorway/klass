package no.ssb.klass.initializer.stabas;

import static com.google.common.base.Preconditions.*;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import no.ssb.klass.core.model.ClassificationFamily;

/**
 * Maps classificationFamilies between Stabas and Klass.
 * <p>
 * Some of the classification families in Stabas are mapped directly to a classification family in Klass. This is the
 * simple case.
 * <p>
 * The other case is that classifications in a classification family shall be mapped to different classification
 * families in Klass, here a map is made per classification.
 */
class ClassificationFamilyMapper {
    private final Map<String, ClassificationFamily> classificationFamilyMap;
    private final Map<String, ClassificationFamily> classificationMap;

    ClassificationFamilyMapper(List<ClassificationFamily> allKlassClassificationFamilies) {
        this.classificationFamilyMap = createClassificationFamilyMap(allKlassClassificationFamilies);
        this.classificationMap = createClassificationMap(allKlassClassificationFamilies);
    }

    public ClassificationFamily getClassificationFamily(String stabasClassificationFamily,
            String classificationNameInNorwegian) {
        checkNotNull(stabasClassificationFamily);
        ClassificationFamily klassClassificationFamily = classificationFamilyMap.get(stabasClassificationFamily);
        if (klassClassificationFamily == null) {
            klassClassificationFamily = classificationMap.get(classificationNameInNorwegian);
            if (klassClassificationFamily == null) {
                throw new IllegalArgumentException(
                        "No mapping to Klass classificationFamily found for stabas classification family: "
                                + stabasClassificationFamily + ", and classification name: "
                                + classificationNameInNorwegian);
            }
        }
        return klassClassificationFamily;
    }

    private Map<String, ClassificationFamily> createClassificationFamilyMap(
            List<ClassificationFamily> allKlassClassificationFamilies) {
        return ImmutableMap.<String, ClassificationFamily> builder()
                .put("01", select("Natur og miljø", allKlassClassificationFamilies))
                .put("04", select("Utdanning", allKlassClassificationFamilies))
                .put("06", select("Arbeid og lønn", allKlassClassificationFamilies))
                .put("08", select("Priser og prisindekser", allKlassClassificationFamilies))
                .put("12", select("Offentlig sektor", allKlassClassificationFamilies))
                .build();
    }

    private Map<String, ClassificationFamily> createClassificationMap(
            List<ClassificationFamily> allKlassClassificationFamilies) {
        return ImmutableMap.<String, ClassificationFamily> builder()
                .put("Standard for regionale spesialkoder", select("Region", allKlassClassificationFamilies))
                .put("Standard for SSBs emneinndeling", select("Internt SSB", allKlassClassificationFamilies))
                .put("Standard for helseregioner", select("Region", allKlassClassificationFamilies))
                .put("Standard for landsdelsinndeling", select("Region", allKlassClassificationFamilies))
                .put("Standard for postdistrikt", select("Region", allKlassClassificationFamilies))
                .put("Standard for fylkeskommune", select("Region", allKlassClassificationFamilies))
                .put("Standard for delområde- og grunnkretsinndeling", select("Region", allKlassClassificationFamilies))
                .put("Klassifisering av kommuner etter innbyggertall", select("Region", allKlassClassificationFamilies))
                .put("Standard for arbeidsgiveravgiftssoner", select("Region", allKlassClassificationFamilies))
                .put("Standard for bosettingstetthet", select("Region", allKlassClassificationFamilies))
                .put("Standard for bydelsinndeling", select("Region", allKlassClassificationFamilies))
                .put("Standard for kommuneinndeling", select("Region", allKlassClassificationFamilies))
                .put("Standard for seksjonsinndeling", select("Internt SSB", allKlassClassificationFamilies))
                .put("Standard for kommuneklassifisering", select("Region", allKlassClassificationFamilies))
                .put("Standard for KOSTRA - kommunegruppering", select("Region", allKlassClassificationFamilies))
                .put("Standard for økonomiske regioner", select("Region", allKlassClassificationFamilies))
                .put("Standard for tettstedsinndeling", select("Region", allKlassClassificationFamilies))
                .put("Standard for sentralitet", select("Region", allKlassClassificationFamilies))
                .put("Standard for politidistrikt", select("Region", allKlassClassificationFamilies))
                .put("Standard for næringstilknytning", select("Region", allKlassClassificationFamilies))
                .put("Standard for fylkesinndeling", select("Region", allKlassClassificationFamilies))
                .put("Standard for land og statsborgerskap i personstatistikk", select("Befolkning",
                        allKlassClassificationFamilies))
                .put("Standard for sivilstand", select("Befolkning", allKlassClassificationFamilies))
                .put("Standard for gruppering av personer etter innvandringsbakgrunn", select(
                        "Innvandring og innvandrere", allKlassClassificationFamilies))
                .put("Standard for gruppering av husholdninger", select("Befolkning", allKlassClassificationFamilies))
                .put("Standard for gruppering av familier", select("Befolkning", allKlassClassificationFamilies))
                .put("Standardkoder for kjønn", select("Befolkning", allKlassClassificationFamilies))
                .put("Standard for klassifisering av fødselsvekt", select("Helse", allKlassClassificationFamilies))
                .put("Standard for type lovbrudd", select("Sosiale forhold og kriminalitet",
                        allKlassClassificationFamilies))
                .put("Gruppering for hjelpe- og omsorgstiltak i barnevernet", select("Sosiale forhold og kriminalitet",
                        allKlassClassificationFamilies))
                .put("Standardkoder for diagnosegrupper (ICPC2)", select("Helse", allKlassClassificationFamilies))
                .put("Grunnbeløpet i folketrygden (gjennomsnitt pr. år)", select("Sosiale forhold og kriminalitet",
                        allKlassClassificationFamilies))
                .put("Grunnbeløpet i folketrygden (beløp pr. 1.5 )", select("Sosiale forhold og kriminalitet",
                        allKlassClassificationFamilies))
                .put("Standard for lovbrudd - justissektoren - lovbrudd 2012", select(
                        "Sosiale forhold og kriminalitet", allKlassClassificationFamilies))
                .put("Standardkoder for europeisk forkortet dødårsaksliste", select("Helse",
                        allKlassClassificationFamilies))
                .put("Standard for lovbrudd - justissektoren - lovlang 2012", select("Sosiale forhold og kriminalitet",
                        allKlassClassificationFamilies))
                .put("Standard for diagnosekapitler", select("Helse", allKlassClassificationFamilies))
                .put("Standard for klassifisering av selvmordsted", select("Helse", allKlassClassificationFamilies))
                .put("Klassifisering av aktivitet på skadested", select("Helse", allKlassClassificationFamilies))
                .put("Standard for klassifisering av skadested", select("Helse", allKlassClassificationFamilies))
                .put("Standard for landkoder utviklingsland_MUL", select("Utenriksøkonomi",
                        allKlassClassificationFamilies))
                .put("Standard for landkoder utenrikshandel", select("Utenriksøkonomi", allKlassClassificationFamilies))
                .put("Variant for medlemsland i den europeiske union", select("Utenriksøkonomi",
                        allKlassClassificationFamilies))
                .put("Variant for verdensdeler benyttet i norsk utenrikshandelsstatistikk", select("Utenriksøkonomi",
                        allKlassClassificationFamilies))
                .put("Standard for landkoder utviklingsland", select("Utenriksøkonomi", allKlassClassificationFamilies))
                .put("Standard for utenrikshandel med varer etter bearbeidingsnivå -  SITC", select("Utenriksøkonomi",
                        allKlassClassificationFamilies))
                .put("Standard for leveringsbetingelser   Incoterms 2000", select("Utenriksøkonomi",
                        allKlassClassificationFamilies))
                .put("Standard for utenrikshandel med varer etter anvendelsesområde- BEC", select("Utenriksøkonomi",
                        allKlassClassificationFamilies))
                .put("Standard for klassifisering av transportmåte ved grensepassering", select("Utenriksøkonomi",
                        allKlassClassificationFamilies))
                .put("Standard for næringsgruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Standard for kjøretøygrupper", select("Transport og reiseliv", allKlassClassificationFamilies))
                .put("Variant av SN - Fløttums 10-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - Fløttums 13-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Standard for PRODCOM koder", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Standard for overføringskapasiteter for internettabonnement", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Standard for transport av farlig gods på vei", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Standard for vareslag i transportstatistikken", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Standard for havnedistrikt", select("Transport og reiseliv", allKlassClassificationFamilies))
                .put("Standard for skadegrad", select("Transport og reiseliv", allKlassClassificationFamilies))
                .put("Standard for trafikanttype", select("Transport og reiseliv", allKlassClassificationFamilies))
                .put("Standard for veitrafikkulykke", select("Transport og reiseliv", allKlassClassificationFamilies))
                .put("Standard for klassifisering av husdyr", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard for bygningstype / GAB", select("Bygg, bolig og eiendom",
                        allKlassClassificationFamilies))
                .put("Standard for internasjonale fangstregioner", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard for viktige norske fangstregioner", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard for fangstarter i sjø", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard for organisasjonsform", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Standard for institusjonell sektorgruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Standard for trafikkenheter innblandet i veitrafikkulykker", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Standard for inndeling av foretak etter størrelsesgruppe", select(
                        "Virksomheter, foretak og regnskap", allKlassClassificationFamilies))
                .put("Standard for CPA koder", select("Nasjonalregnskap og konjunkturer",
                        allKlassClassificationFamilies))
                .put("Standard for klassifisering av plantevernmiddel etter bruksområde", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard for alkoholholdige drikkevarer", select("Varehandel og tjenesteyting",
                        allKlassClassificationFamilies))
                .put("Standard for klassifisering av dyrka og dyrkbar jord tillatt omdisponert til andre formål enn jordbruk, etter hovedformål",
                        select("Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard for institusjoner / GAB", select("Helse", allKlassClassificationFamilies))
                .put("Variant av SN - Fløttums 19-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - Fløttums 21-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Standard for oppvarmingskilde", select("Bygg, bolig og eiendom", allKlassClassificationFamilies))
                .put("Standard for klassifisering av omsetningstype ved tinglyst omsetning av fast eiendom", select(
                        "Bygg, bolig og eiendom", allKlassClassificationFamilies))
                .put("Standard for klassifisering av skogeiere med minst 25 dekar produktivt skogareal i eiergrupper",
                        select("Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard for klassifisering av kjøpere og selgere av tømmer til industriformål", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard for treslag- og sortimentsinndeling ved tømmersalg", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard for klassifisering av jordbruksbedrifter etter størrelse", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard for klassifisering av jordbruksbedrifter etter driftsform", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard for klassifisering av jordbruksareal etter bruken", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard for markslagsklassifikasjon i Økonomisk kartverk", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Variant av institusjonell sektorgruppering - intern versjon", select(
                        "Nasjonalregnskap og konjunkturer", allKlassClassificationFamilies))
                .put("Variant av institusjonell sektorgruppering - S950", select("Nasjonalregnskap og konjunkturer",
                        allKlassClassificationFamilies))
                .put("Standard for lastetype i sjøtransport", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Standard klassifisering av type reisemål", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Standard klassifisering av registrert avgang av vilt utenom ordinær jakt", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard klassifisering av hjortevilt etter alder og kjønn", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard klassifisering av produktivt skogareal etter hogstklasse", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard klassifisering av produktivt skogareal etter bonitet (H40)", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard  klassifisering av skogeiendommer etter størrelsesklasse", select(
                        "Jord, skog, jakt og fiskeri", allKlassClassificationFamilies))
                .put("Standard klassifisering av skogsveger for motorkjøretøyer", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard klassifisering av småvilt", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard klassifisering av villreinområder", select("Jord, skog, jakt og fiskeri",
                        allKlassClassificationFamilies))
                .put("Standard for skipstyper", select("Transport og reiseliv", allKlassClassificationFamilies))
                .put("Standard for førerkortklasse", select("Transport og reiseliv", allKlassClassificationFamilies))
                .put("Variant av SN - 3-hovedkategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - 10-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - 64-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - næringshovedområde kategorigruppering", select(
                        "Virksomheter, foretak og regnskap", allKlassClassificationFamilies))
                .put("Variant av SN - næring kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - 12-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - 43-kategoriegruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Variant av SN - Miljøregnskap", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .put("Standard for klassifisering av reisetype", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Standard for klassifisering av IKT-sikkerhetstiltak", select("Varehandel og tjenesteyting",
                        allKlassClassificationFamilies))
                .put("Standard for klassifisering av informasjonssektoren", select("Transport og reiseliv",
                        allKlassClassificationFamilies))
                .put("Variant av SN - Fløttums  3-kategorigruppering", select("Virksomheter, foretak og regnskap",
                        allKlassClassificationFamilies))
                .build();
    }

    private ClassificationFamily select(String classificationFamily,
            List<ClassificationFamily> allKlassClassificationFamilies) {
        return allKlassClassificationFamilies.stream()
                .filter(family -> family.getName().equals(classificationFamily))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Klass has no classification family with name: "
                        + classificationFamily));
    }

}
