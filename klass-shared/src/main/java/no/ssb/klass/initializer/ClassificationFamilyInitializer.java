package no.ssb.klass.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.util.Translatable;

/**
 * Populates database with classification families.
 * <p>
 * Note is only run if no classification families currently exists in database.
 */
@Service
public class ClassificationFamilyInitializer {
    private final ClassificationService classificationService;

    @Autowired
    public ClassificationFamilyInitializer(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @Transactional
    public void run() {
        if (classificationService.findAllClassificationFamilies().isEmpty()) {
            populate();
        }
    }

    private void populate() {
        classificationService.saveClassificationFamily(createClassificationFamily("Arbeid og lønn", "Arbeid og løn",
                "Labour market and earnings", "ikon_arbeid.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Bank og finansmarked",
                "Bank og finansmarknad", "Banking and financial markets", "ikon_bank.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Befolkning", "Befolkning",
                "Population", "ikon_befolkning.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Bygg, bolig og eiendom",
                "Bygg, bustad og eigedom", "Construction, housing and property", "ikon_bygg.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Energi og industri",
                "Energi og industri", "Energy and manufacturing", "ikon_energi.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Helse", "Helse", "Health",
                "ikon_helse.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Inntekt og forbruk",
                "Inntekt og forbruk", "Income and consumption", "ikon_inntekt.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Innvandring og innvandrere",
                "Innvandring og innvandrarar", "Immigration and immigrants", "ikon_innvandring.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Jord, skog, jakt og fiskeri",
                "Jord, skog, jakt og fiskeri", "Agriculture, forestry, hunting and fishing", "ikon_jord.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Kultur og fritid",
                "Kultur og fritid", "Culture and recreation", "ikon_kultur.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Nasjonalregnskap og konjunkturer",
                "Nasjonalrekneskap og konjunkturar", "National accounts and business cycles",
                "ikon_nasjonalregnskap.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Natur og miljø", "Natur og miljø",
                "Nature and environment", "ikon_natur.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Offentlig sektor",
                "Offentleg sektor", "Public sector", "ikon_offentlig.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Priser og prisindekser",
                "Prisar og prisindeksar", "Prices and price indices", "ikon_priser.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Region", "Region", "Region",
                "ikon_kart.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Sosiale forhold og kriminalitet",
                "Sosiale forhold og kriminalitet", "Social conditions, welfare and crime", "ikon_sosiale.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Svalbard", "Svalbard", "Svalbard",
                "ikon_svalbard.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Teknologi og innovasjon",
                "Teknologi og innovasjon", "Technology and innovation", "ikon_teknologi.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Transport og reiseliv",
                "Transport og reiseliv", "Transport and tourism", "ikon_transport.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Utdanning", "Utdanning", "Education",
                "ikon_utdanning.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Utenriksøkonomi", "Utanriksøkonomi",
                "External economy", "ikon_utenriks.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Valg", "Val", "Elections",
                "ikon_valg.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Varehandel og tjenesteyting",
                "Varehandel og tenesteyting", "Wholesale and retail trade and service activities",
                "ikon_varehandel.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Virksomheter, foretak og regnskap",
                "Verksemder, føretak og rekneskap", "Establishments, enterprises and accounts",
                "ikon_virksomheter.svg"));
        classificationService.saveClassificationFamily(createClassificationFamily("Internt SSB", "Internt SSB",
                "For internal use Statistics Norway", "ikon_ssb.svg"));
    }

    private ClassificationFamily createClassificationFamily(String nameNO, String nameNN, String nameEN,
            String iconName) {
        return new ClassificationFamily(new Translatable(nameNO, nameNN, nameEN), iconName);
    }
}
