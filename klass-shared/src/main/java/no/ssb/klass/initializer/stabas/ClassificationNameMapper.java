package no.ssb.klass.initializer.stabas;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;

/**
 * Some classifications in Stabas have names that deviates from the standard where classifications shall start with
 * 'Standard for ...'. All Stabas classifications whose names deviates are mapped to a new name that follows the new
 * standard.
 */
public class ClassificationNameMapper {
    private final Map<String, Translatable> classificationNameMap;

    public ClassificationNameMapper() {
        this.classificationNameMap = createClassificationNameMap();
    }

    /**
     * Maps an incorrect classification name in Stabas to a correct classification name to be used by Klass.
     * 
     * @param classificationName
     * @return a corrected classificationName or same as input classificationName if no mapping exists
     */
    public Translatable mapClassificationName(Translatable classificationName) {
        return classificationNameMap.getOrDefault(classificationName.getString(Language.NB), classificationName);
    }

    private Map<String, Translatable> createClassificationNameMap() {
        return ImmutableMap.<String, Translatable> builder()
                .put("Grunnbeløpet i folketrygden (beløp pr. 1.5 )", createTranslatable(
                        "Standard for grunnbeløpet i folketrygden (beløp pr. 1.5 )",
                        "Standard for grunnbeløpet i folketrygda (gjennomsnitt pr. år)",
                        "Classification of basic amount of the National Insurance Scheme (B.a)  - average"))
                .put("Gruppering for hjelpe- og omsorgstiltak i barnevernet", createTranslatable(
                        "Standard for gruppering av hjelpe- og omsorgstiltak i barnevernet",
                        "Standard for gruppering av hjelpe- og omsorgstiltak i barnevernet",
                        "Classification of intervention in Child Welfare"))
                .put("Grunnbeløpet i folketrygden (gjennomsnitt pr. år)", createTranslatable(
                        "Standard for grunnbeløpet i folketrygden (gjennomsnitt pr. år)",
                        "Standard for grunnbeløpet i folketrygda",
                        "Classification of basic amount of the National Insurance Scheme (B.a)"))
                .put("Standard klassifisering av type reisemål", createTranslatable(
                        "Standard for klassifisering av type reisemål",
                        "Standard for klassifisering av type reisemål",
                        "Classification of type of destination"))
                .put("Norsk standard for utdanningsgruppering (NUS)", createTranslatable(
                        "Standard for utdanningsgruppering (NUS)",
                        "Standard for utdanningsgruppering (NUS)",
                        "Classification of education (NUS)"))
                .put("Standardkoder for kjønn", createTranslatable(
                        "Standard for kjønn",
                        "Standard for kjønn",
                        "Classification of sex"))
                .put("Klassifisering av aktivitet på skadested", createTranslatable(
                        "Standard for klassifisering av aktivitet på skadested",
                        "Standard for klassifisering av aktivitet på skadestad",
                        "Classification of activity at place of death"))
                .put("Standardkoder for diagnosegrupper (ICPC2)", createTranslatable(
                        "Standard for diagnosegrupper (ICPC2)",
                        "Standard for diagnosegrupper",
                        "Classification of diagnosis groups"))
                .put("Standardkoder for europeisk forkortet dødårsaksliste", createTranslatable(
                        "Standard for europeisk forkortet dødårsaksliste",
                        "Standard for europeisk forkortet dødårsaksliste",
                        "Classification of European short-list for causes of death"))
                .put("Standard  klassifisering av skogeiendommer etter størrelsesklasse", createTranslatable(
                        "Standard for klassifisering av skogeiendommer etter størrelsesklasse",
                        "Standard for klassifisering av skogeigedommar etter storleiksklasse",
                        "Classification of forest properties by size class"))
                .put("Standard klassifisering av hjortevilt etter alder og kjønn", createTranslatable(
                        "Standard for klassifisering av hjortevilt etter alder og kjønn",
                        "Standard for klassifisering av hjortevilt etter alder og kjønn",
                        "Classification of cervids after age and sex"))
                .put("Standard klassifisering av produktivt skogareal etter bonitet (H40)", createTranslatable(
                        "Standard for klassifisering av produktivt skogareal etter bonitet (H40)",
                        "Standard for klassifisering av produktivt skogareal etter bonitet (H40)",
                        "Classification of productive forest area by site quality (H40)"))
                .put("Standard klassifisering av produktivt skogareal etter hogstklasse", createTranslatable(
                        "Standard for klassifisering av produktivt skogareal etter hogstklasse",
                        "Standard for klassifisering av produktivt skogareal etter hogstklasse",
                        "Classification of productive forest area by development class"))
                .put("Standard klassifisering av registrert avgang av vilt utenom ordinær jakt", createTranslatable(
                        "Standard for klassifisering av registrert avgang av vilt utenom ordinær jakt",
                        "Standard for klassifisering av registrert avgang av vilt utanom ordinær jakt",
                        "Classification of registered non-harvest mortality of cervids and large carnivores, by cause"))
                .put("Standard klassifisering av skogsveger for motorkjøretøyer", createTranslatable(
                        "Standard for klassifisering av skogsveger for motorkjøretøyer",
                        "Standard for klassifisering av skogsvegar for motorkøyrety",
                        "Classification of forest roads"))
                .put("Standard klassifisering av småvilt", createTranslatable(
                        "Standard for klassifisering av småvilt",
                        "Standard for klassifisering av småvilt",
                        "Classification of small game species"))
                .put("Standard klassifisering av villreinområder", createTranslatable(
                        "Standard for klassifisering av villreinområder",
                        "Standard for klassifisering av villreinområda",
                        "Classification of wild reindeer areas"))
                .put("Standard av renseprinsipper for store avløpsanlegg (>50 pe)", createTranslatable(
                        "Standard for renseprinsipper for store avløpsanlegg (>50 pe)",
                        "Standard for reinseprinsipp for store avløpsanlegg (>50 pe)",
                        "Classification of treatment methods for large wastewater treatment plants (>50 pe)"))
                .put("Klassifisering av kommuner etter innbyggertall", createTranslatable(
                        "Standard for klassifisering av kommuner etter innbyggertall",
                        "Standard for klassifisering av kommunar etter innbyggjartal",
                        "Classification of municipalities by population size"))
                .put("Standard for historisk kode (NUS)", createTranslatable(
                        "Standard for historisk kode (NUS)",
                        "Standard for historisk kode (NUS)",
                        "Classification of historic code (NUS)"))
                .put("Standard for PRODCOM koder", createTranslatable(
                        "Standard for PRODCOM koder",
                        "Standard for PRODCOM kodar",
                        "Classification of codes for PRODCOM"))
                .put("Standard for COFOG koder", createTranslatable(
                        "Standard for COFOG koder",
                        "Standard for COFOG kodar",
                        "Classification of COFOG codes"))
                .put("Standard for CPA koder", createTranslatable(
                        "Standard for CPA koder",
                        "Standard for CPA kodar",
                        "Classification of CPA codes"))
                .put("Standard for landkoder utenrikshandel", createTranslatable(
                        "Standard for landkoder utenrikshandel",
                        "Standard for landkodar utenrikshandel",
                        "Classification of land codes used in foreign trade stats"))
                .put("Standard for leveringsbetingelser   Incoterms 2000", createTranslatable(
                        "Standard for leveringsbetingelser   Incoterms 2000",
                        "Standard for leveringsvilkår   Incoterms 2000",
                        "Classification of delivery terms in international trade, Incoterms 2000"))
                .put("Standard for utenrikshandel med varer etter bearbeidingsnivå -  SITC", createTranslatable(
                        "Standard for utenrikshandel med varer etter bearbeidingsnivå -  SITC",
                        "Standard for utanrikshandel med varer etter tilverknadsnivå -  SITC",
                        "Classification of external trade in goods by levels of processing - SITC"))
                .put("Standard for landkoder utviklingsland", createTranslatable(
                        "Standard for landkoder utviklingsland",
                        "Standard for landkodar utviklingsland",
                        "Classification of land codes for developing countries"))
                .put("Standard for landkoder utviklingsland_MUL", createTranslatable(
                        "Standard for landkoder utviklingsland_MUL",
                        "Standard for landkodar utviklingsland_MUL",
                        "Classification of land codes for developing countries_MUL"))
                .put("Standard for vareslag i transportstatistikken", createTranslatable(
                        "Standard for vareslag i transportstatistikken",
                        "Standard for vareslag i transportstatistikken",
                        "Classification of goods for transport statistic"))
                .put("Standard for kodetype (NUS)", createTranslatable(
                        "Standard for kodetype (NUS)",
                        "Standard for kodetype (NUS)",
                        "Classification of type of code (NUS)"))
                .put("Standard for studiepoeng (NUS)", createTranslatable(
                        "Standard for studiepoeng (NUS)",
                        "Standard for studiepoeng (NUS)",
                        "Classification of credit points (NUS)"))
                .put("Standard for klassetrinn (NUS)", createTranslatable(
                        "Standard for klassetrinn (NUS)",
                        "Standard for klassetrinn (NUS)",
                        "Classification of class level (NUS)"))
                .put("Standard for varighet (NUS)", createTranslatable(
                        "Standard for varighet (NUS)",
                        "Standard for tidslengd (NUS)",
                        "Classification of duration (NUS)"))
                .put("Standard for studieretning/utdanningsprogram (NUS)", createTranslatable(
                        "Standard for studieretning/utdanningsprogram (NUS)",
                        "Standard for studieretning/utdanningsprogram (NUS)",
                        "Classification of areas of study- /education programmes (NUS)"))
                .put("Standard for kompetansekode (NUS)", createTranslatable(
                        "Standard for kompetansekode (NUS)",
                        "Standard for kompetansekode (NUS)",
                        "Classification of competence code (NUS)"))
                .put("Standard for universitets- og høgskolegruppering (NUS)", createTranslatable(
                        "Standard for universitets- og høgskolegruppering (NUS)",
                        "Standard for universitets- og høgskulegruppering (NUS)",
                        "Classification of tertiary classification (NUS)"))
                .put("Standard for gradmerke (NUS)", createTranslatable(
                        "Standard for gradmerke (NUS)",
                        "Standard for gradmerke (NUS)",
                        "Classification of grade mark (NUS)"))
                .put("Standard for ISCED97 destinasjon (NUS)", createTranslatable(
                        "Standard for ISCED97 destinasjon (NUS)",
                        "Standard for ISCED97 destinasjon (NUS)",
                        "Classification of ISCED97 destination (NUS)"))
                .put("Standard for ISCED97 orientering (NUS)", createTranslatable(
                        "Standard for ISCED97 orientering (NUS)",
                        "Standard for ISCED97 orientering (NUS)",
                        "Classification of ISCED97 orientation (NUS)"))
                .put("Standard for ISCED97 varighet (NUS)", createTranslatable(
                        "Standard for ISCED97 varighet (NUS)",
                        "Standard for ISCED97 varighet (NUS)",
                        "Classification of ISCED97 duration (NUS)"))
                .put("Standard for ISCED97 grad (NUS)", createTranslatable(
                        "Standard for ISCED97 grad (NUS)",
                        "Standard for ISCED97 grad (NUS)",
                        "Classification of post-secondary vocational credit points (NUS)"))
                .put("Standard for ISCED97 nivå og fagfelt (NUS)", createTranslatable(
                        "Standard for ISCED97 nivå og fagfelt (NUS)",
                        "Standard for ISCED97 nivå og fagfelt (NUS)",
                        "Classification of ISCED97 Level and Fields of Education and Training (NUS)"))
                .put("Standard for seksjonsinndeling", createTranslatable(
                        "Standard for seksjonsinndeling",
                        "Standard for seksjonsinndeling",
                        "Classification of organisational units"))
                .put("Standard for klassifisering av transportmåte ved grensepassering", createTranslatable(
                        "Standard for klassifisering av transportmåte ved grensepassering",
                        "Standard for transportmåtar ved grensepassering",
                        "Classification of mode of transport when crossing the border"))
                .put("Standard for klassifisering av reisetype", createTranslatable(
                        "Standard for klassifisering av reisetype",
                        "Standard for klassifisering av reisetype",
                        "Classification of types of travels"))
                .put("Standard for klassifisering av IKT-sikkerhetstiltak", createTranslatable(
                        "Standard for klassifisering av IKT-sikkerhetstiltak",
                        "Standard for klassifisering av IKT-tryggingstiltak",
                        "Classification of IT-security facilities"))
                .put("Standard for klassifisering av informasjonssektoren", createTranslatable(
                        "Standard for klassifisering av informasjonssektoren",
                        "Standard for klassifisering av informasjonssektoren",
                        "Classification of the information sector"))
                .put("Standard for kjøretøygrupper", createTranslatable(
                        "Standard for kjøretøygrupper",
                        "Standard for kjøretygruppe",
                        "Classification of vehicle groups"))
                .put("Standard for KOSTRA - kommunegruppering", createTranslatable(
                        "Standard for KOSTRA - kommunegruppering",
                        "Standard for KOSTRA - kommunegruppering",
                        "Classification of KOSTRA - municipalities"))
                .put("Standard for kurstrinnskode (NUS)", createTranslatable(
                        "Standard for kurstrinnskode (NUS)",
                        "Standard for kurstrinnskode (NUS)",
                        "Classification of course level  code (NUS)"))
                .put("Standard for klassifisering av energiprodukter", createTranslatable(
                        "Standard for klassifisering av energiprodukter",
                        "Standard for klassifisering av energiprodukt",
                        "Classification of energy products"))
                .put("Standard for klassifisering av arealer til statistikkformål", createTranslatable(
                        "Standard for klassifisering av arealer til statistikkformål",
                        "Standard for klassifisering av areal til statistikkformål",
                        "Classification of land use and land cover"))
                .put("Standard for utenrikshandel med varer etter anvendelsesområde- BEC", createTranslatable(
                        "Standard for utenrikshandel med varer etter anvendelsesområde- BEC",
                        "Standard for utanrikshandel med varer etter brukssområde- BEC",
                        "Classification of external trade in goods, by groups of end use (BEC)"))
                .put("Standard for fagskoleutdanning (NUS)", createTranslatable(
                        "Standard for fagskoleutdanning (NUS)",
                        "Standard for fagskoleutdanning (NUS)",
                        "Classification of vocational school education (NUS)"))
                .put("Standard for førerkortklasse", createTranslatable(
                        "Standard for førerkortklasse",
                        "Standard for førarkortklasse",
                        "Classification of driving licence classes"))
                .put("Standard for gruppering av personer etter innvandringsbakgrunn", createTranslatable(
                        "Standard for gruppering av personer etter innvandringsbakgrunn",
                        "Standard for gruppering av personar etter innvandringsbakgrunn",
                        "Classification of immigration categories"))
                .put("Standard for land og statsborgerskap i personstatistikk", createTranslatable(
                        "Standard for land og statsborgerskap i personstatistikk",
                        "Standard for land og statsborgarskap i personstatistikk",
                        "Classification of country and citizenship in population statistics"))
                .put("Standard for bydelsinndeling", createTranslatable(
                        "Standard for bydelsinndeling",
                        "Standard for bydelsinndeling",
                        "Classification of urban districts"))
                .put("Standard for diagnosekapitler", createTranslatable(
                        "Standard for diagnosekapitler",
                        "Standard for diagnosekapittel",
                        "Classification of diagnosis chapters 2004"))
                .put("Standard for kommuneinndeling", createTranslatable(
                        "Standard for kommuneinndeling",
                        "Standard for kommuneinndeling",
                        "Classification of municipalities"))
                .put("Standard for kommuneklassifisering", createTranslatable(
                        "Standard for kommuneklassifisering",
                        "Standard for kommuneklassifisering",
                        "Classification of municipality groups"))
                .build();
    }

    private Translatable createTranslatable(String no, String nn, String en) {
        return new Translatable(no, nn, en);
    }
}
