package no.ssb.klass.designer.testbench;

import java.io.File;

import no.ssb.klass.solr.config.KlassSearchTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.google.common.base.Strings;
import com.vaadin.testbench.HasDriver;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.ScreenshotOnFailureRule;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.designer.testbench.pages.ClassificationFamilyPage;
import no.ssb.klass.designer.testbench.pages.LoginPage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AbstractTestBench.Config.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { KlassSearchTestConfiguration.class })
@ActiveProfiles({ ConfigurationProfiles.H2_INMEMORY, ConfigurationProfiles.AD_AUTHENTICATE_OFFLINE,
        ConfigurationProfiles.SMALL_IMPORT, ConfigurationProfiles.MOCK_MAILSERVER })
// Need to explicitly set @TestExecutionListeners to get rid of
// ResetMocksTestExecutionListener which fails in
// combination with Vaadin
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public abstract class AbstractTestBench implements HasDriver {
    protected static final String STANDARD_FOR_POLITIDISTRIKT = "Standard for politidistrikt";
    protected static final String POLITIDISTRIKT_2002 = "Politidistrikt 2002";
    protected static final String POLITIDISTRIKT_2016 = "Politidistrikt 2016";
    protected static final String POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006 = "Politidistrikt 2002 - Kommuneinndeling 2006";
    protected static final String STANDARD_FOR_KOMMUNEINNDELING = "Standard for kommuneinndeling";
    protected static final String KOMMUNEINNDELING_2002 = "Kommuneinndeling 2002";
    private WebDriver driver;
    @Value("${local.server.port}")
    private int port = 8080;
    /**
     * Grabs a screenshot whenever a test fails and quits the driver after the test.
     */
    @Rule
    public ScreenshotOnFailureRule screenshotOnFailure = new ScreenshotOnFailureRule(this, true);

    @Before
    public void setUp() throws Exception {
        if (Strings.nullToEmpty(System.getProperty("useHeadless")).equals("true")) {
            driver = new PhantomJSDriver();
        } else {
            driver = new FirefoxDriver();
            /*
            driver = new FirefoxDriver(new FirefoxBinary(new File(
                    "C:\\Users\\mlo\\Downloads\\FirefoxPortable\\FirefoxPortable.exe")), new FirefoxProfile());
             */
        }
        driver.manage().window().setSize(new Dimension(1280, 800));
        Parameters.setScreenshotErrorDirectory("target/testbench/errors");
        Parameters.setScreenshotReferenceDirectory("target/testbench/reference");
    }

    public ClassificationFamilyPage getHomePage() {
        driver.get("http://localhost:" + port + "/klassui");
        if (driver.getCurrentUrl().contains("login")) {
            return new LoginPage(getDriver()).loginAsAdmin();
        }
        return new ClassificationFamilyPage(getDriver());
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = {
            "no.ssb.klass.core",
            "no.ssb.klass.initializer",
            "no.ssb.klass.designer"
    })
    static class Config {
    }
}
