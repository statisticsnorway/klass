package no.ssb.klass.solr.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrXmlConfig;
import org.springframework.context.annotation.*;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile("!mock-search")
@Import(KlassSearchConfiguration.class)
public class KlassSearchTestConfiguration {

    @Bean
    @Primary
    public SolrClient embeddedSolrServerAndClient() throws IOException, SolrServerException {
        CoreContainer coreContainer;
        try {
            String solrTempWorkspace = org.assertj.core.util.Files.newTemporaryFolder().getAbsolutePath() + "/klassTests";
            exportResource("solr/embedded/solr.xml", solrTempWorkspace);
            exportResource("solr/embedded/Klass/core.properties", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/schema.xml", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/solrconfig.xml", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/stoppord.txt", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/synonymer.txt", solrTempWorkspace + "/klass");
            coreContainer = new CoreContainer(SolrXmlConfig.fromSolrHome(new File(solrTempWorkspace).toPath(), null));
            coreContainer.load();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
        SolrClient server = new EmbeddedSolrServer(coreContainer, "Klass");
        server.deleteByQuery("*:*");
        return server;
    }

    private void exportResource(String resourcePath, String targetPath) throws Exception {
        File dest = new File(targetPath + "/" + new File(resourcePath).getName());
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        FileUtils.copyInputStreamToFile(resourceAsStream, dest);
    }

}

