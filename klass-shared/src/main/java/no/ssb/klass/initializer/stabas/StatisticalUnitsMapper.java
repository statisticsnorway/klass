package no.ssb.klass.initializer.stabas;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;

/**
 * Maps statisticalUnits between Stabas and Klass
 */
class StatisticalUnitsMapper {
    private final Map<String, List<StatisticalUnit>> stabasToKlassMap;
    private final List<StatisticalUnit> allKlassStatisticalUnits;

    StatisticalUnitsMapper(List<StatisticalUnit> allKlassStatisticalUnits) {
        this.allKlassStatisticalUnits = allKlassStatisticalUnits;
        this.stabasToKlassMap = createStatisticalUnitMap();
    }

    public List<StatisticalUnit> getStatisticalUnits(String stabasStatisticalUnit) {
        checkNotNull(stabasStatisticalUnit);
        List<StatisticalUnit> klassStatisticalUnits = stabasToKlassMap.get(stabasStatisticalUnit);
        if (klassStatisticalUnits == null) {
            throw new IllegalArgumentException("No mapping to Klass statisticalUnits found for: "
                    + stabasStatisticalUnit);
        }
        return klassStatisticalUnits;
    }

    private Map<String, List<StatisticalUnit>> createStatisticalUnitMap() {
        return ImmutableMap.<String, List<StatisticalUnit>> builder()
                .put("Aktiviteter", select("Person"))
                .put("Alkoholholdige drikkevarer", select("Produkt/vare"))
                .put("Arbeidsoppgaver", select("Person"))
                .put("Areal", select("Eiendom/areal"))
                .put("Areal (markslagsfigur)", select("Eiendom/areal"))
                .put("Boliger omsatt i fritt salg.", select("Bolig"))
                .put("Boliger omsatt til markedspris", select("Bolig"))
                .put("Bygninger", select("Bygning"))
                .put("Diagnoser", select("Person"))
                .put("Dyr", select("Dyr"))
                .put("Economic activities", select("Foretak"))
                .put("Eiendomstyper", select("Eiendom/areal"))
                .put("En varegruppering som beskriver ulike varers klassifisering etter forskjellige anvendelsesområder.",
                        select("Produkt/vare"))
                .put("Familier", select("Familie"))
                .put("Fangstarter (sjø)", select("Dyr"))
                .put("Foretak", select("Foretak"))
                .put("Førerkort", select("Person"))
                .put("Havner", select("Region"))
                .put("Helseregioner", select("Region"))
                .put("Her er varene gruppert etter bearbeidingsnivå (råvarer, halvfabrikata og ferdigvarer).",
                        select("Produkt/vare"))
                .put("Husdyr", select("Dyr"))
                .put("Husholdninger", select("Husholdning"))
                .put("Ikke relevant", select("Person"))
                .put("Institusjoner", select("Foretak"))
                .put("Internasjonale fangstregioner", select("Region"))
                .put("Internationale handelstransaksjoner", select("Produkt/vare"))
                .put("Internett-abonnement", select("Produkt/vare"))
                .put("Institusjonell enhet", select("Institusjonell enhet"))
                .put("Jordbruksareal", select("Eiendom/areal"))
                .put("Jordbruksbedrifter", select("Foretak"))
                .put("Juridiske virksomheter", select("Foretak"))
                .put("Kjøpere og selgere", select("Foretak"))
                .put("Kjøretøy", select("Kjøretøy"))
                .put("Kommuner", select("Region"))
                .put("Land", select("Region"))
                .put("Land (oftest opprinnelses og bestemmelsesland) i utenrikshandelsstatistikken.", select(
                        "Region"))
                .put("Land eller statsborgerskap.  Et land er her definert som et avgrenset territorium som ofte oppfattes som en egen enhet.  "
                        + "Et land er altså ikke nødvendigvis en selvstendig stat.", select("Person", "Region"))
                .put("Omsetningstyper", select("Foretak"))
                .put("Oppvarmingskilder", select("Bolig", "Bygning"))
                .put("Ordrereserve", select("Foretak", "Region"))
                .put("Ordretilgang, ordrereserve og produksjon", select("Foretak"))
                .put("Person", select("Person"))
                .put("Personer", select("Person"))
                .put("Produkt", select("Produkt/vare"))
                .put("Produkter", select("Produkt/vare"))
                .put("Produkter etter hvilke samfunnsforhold de belyser", select("Produkt/vare"))
                .put("Produktivt skogareal", select("Eiendom/areal"))
                .put("Region", select("Region"))
                .put("Regionale enheter", select("Region"))
                .put("Regionale spesialkoder", select("Region"))
                .put("Reise", select("Reise"))
                .put("Seksjoner", select("Foretak"))
                .put("Skip", select("Fartøy/skip"))
                .put("Skogeiendommer", select("Eiendom/areal"))
                .put("Skogeiere", select("Foretak"))
                .put("Skogsveg", select("Eiendom/areal"))
                .put("Sykdommer", select("Person"))
                .put("Tiltak", select("Person"))
                .put("Trafikkenheter", select("Ulykke"))
                .put("Treslag og sortiment", select("Produkt/vare"))
                .put("Utdanningsaktiviteter som har en formell skolemessig ramme, innrettet mot en systematisk formidling av "
                        + "kunnskap og ferdigheter, og som har en viss lengde.",
                        select("Utdanning/kurs"))
                .put("Varegrupper", select("Produkt/vare"))
                .put("Vareslag", select("Produkt/vare"))
                .put("Viktige norske fangstregioner", select("Region"))
                .put("yrkestitler", select("Person"))
                .put("Økonomiske virksomheter", select("Foretak"))
                .put("Økonomisk transaksjon", select("Økonomisk transaksjon"))

                .build();
    }

    private List<StatisticalUnit> select(String... statisticalUnits) {
        List<StatisticalUnit> result = new ArrayList<>();
        for (String statisticalUnit : statisticalUnits) {
            result.add(allKlassStatisticalUnits.stream()
                    .filter(unit -> unit.getName(Language.NB).equals(statisticalUnit))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Klass has no statisticalUnit with name: "
                            + statisticalUnit)));
        }
        return result;
    }
}
