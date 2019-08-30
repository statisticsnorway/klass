package no.ssb.klass.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import no.ssb.klass.initializer.stabas.StabasInitializer;

/**
 * Populates an empty database with required data such as classification families and statistical units. And creates
 * classifications migrated from Stabas, etc.
 * <p>
 * Has Ordered.HIGHEST_PRECEDENCE so that it is run before the SearchIndexPopulator. Hence any created (migrated)
 * classifications will later be indexed.
 */
@Service
// HIGHEST_PRECEDENCE so that it is run before the SearchIndexPopulator
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class MainInitializer implements CommandLineRunner {
    private final StabasInitializer stabasInitializer;
    private final StatisticalUnitInitializer statisticalUnitInitializer;
    private final ClassificationFamilyInitializer classificationFamilyInitializer;

    @Autowired
    public MainInitializer(StabasInitializer stabasInitializer, StatisticalUnitInitializer statisticalUnitInitializer,
            ClassificationFamilyInitializer classificationFamilyInitializer) {
        this.stabasInitializer = stabasInitializer;
        this.statisticalUnitInitializer = statisticalUnitInitializer;
        this.classificationFamilyInitializer = classificationFamilyInitializer;
    }

    @Override
    public void run(String... args) throws Exception {
        statisticalUnitInitializer.run();
        classificationFamilyInitializer.run();
        stabasInitializer.run();
    }
}
