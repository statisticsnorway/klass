package no.ssb.klass.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.util.Translatable;

/**
 * Populates database with statistical units.
 * <p>
 * Note is only run if no statistical units currently exists in database.
 */
@Service
public class StatisticalUnitInitializer {
    private final ClassificationService classificationService;

    @Autowired
    public StatisticalUnitInitializer(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @Transactional
    public void run() {
        if (classificationService.findAllStatisticalUnits().isEmpty()) {
            populate();
        }
    }

    private void populate() {
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Arbeidsforhold",
                "Arbeidstilhøve", "Employment")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Avfallsanlegg", "Avfallsanlegg",
                "Waste treatment plant")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Bolig", "Bustad", "Dwelling")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Bygning", "Bygning",
                "Builing")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Dyr", "Dyr", "Animal")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Familie", "Familie",
                "Family")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Fartøy/skip", "Fartøy/skip",
                "Vessel/ship")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Foretak", "Foretak",
                "Enterprise")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Eiendom/areal", "Eigedom/areal",
                "Property/area")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Husholdning", "Hushald",
                "Household")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Institusjonell enhet",
                "Institusjonell eining", "Institutional unit")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Kjøretøy", "Kjøretøy",
                "Vehicle")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Utdanning/kurs",
                "Utdanning/kurs", "Education/course")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Person", "Person", "Person")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Produkt/vare", "Produkt/vare",
                "Product/goods")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Region", "Region", "Region")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Reise", "Reise", "Travel")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Ulykke", "Ulukke",
                "Accident")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Virksomhet", "Verksemd",
                "Establishment")));
        classificationService.saveStatisticalUnit(new StatisticalUnit(new Translatable("Økonomisk transaksjon",
                "Økonomisk transaksjon", "Economic transaction")));
    }
}
