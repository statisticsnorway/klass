package no.ssb.klass.solr.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.apache.solr.core.CoreContainer;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile("!mock-search")
public class KlassSearchConfiguration implements AsyncConfigurer {

    @NotEmpty
    @Value("${klass.env.search.solr.url}")
    protected String solrUrl;
    @NotEmpty
    @Value("${klass.env.search.solr.core}")
    protected String solrCore;

    @Bean
    @Profile("remote-solr")
    public SolrClient solrClient() {
        HttpSolrClient httpSolrClient = new SolrBackwardsCompatibleHttpClient(solrUrl);
        httpSolrClient.setRequestWriter(new RequestWriter());
        return httpSolrClient;
    }

    @Bean
    @Profile("embedded-solr")
    public SolrClient embeddedSolrServerAndClient() {
        CoreContainer coreContainer;
        try {
            // File solrTempWorkspace = Files.createTempDirectory("klassSolr").toFile();
            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            String solrTempWorkspace = new File(baseDir.getAbsolutePath() + "/KlassSolrTempStorage").getAbsolutePath();
            exportResource("solr/embedded/solr.xml", solrTempWorkspace);
            exportResource("solr/embedded/Klass/core.properties", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/schema.xml", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/solrconfig.xml", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/stoppord.txt", solrTempWorkspace + "/klass");
            exportResource("solr/embedded/Klass/synonymer.txt", solrTempWorkspace + "/klass");
            coreContainer = new CoreContainer(solrTempWorkspace);
            coreContainer.load();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
        SolrClient server = new EmbeddedSolrServer(coreContainer, "Klass");
        return server;
    }

    private void exportResource(String resourcePath, String targetPath) throws Exception {
        File dest = new File(targetPath + "/" + new File(resourcePath).getName());
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        FileUtils.copyInputStreamToFile(resourceAsStream, dest);
    }

    @Bean
    public SolrTemplate solrCore2Template(SolrClient solrClient) {
        SolrTemplate solrTemplate = new SolrTemplate(solrClient, solrCore);
        return solrTemplate;
    }

    // limiting number of threads to avoid solr maxWarmingSearchers problem
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    protected static class SolrBackwardsCompatibleHttpClient extends HttpSolrClient {

        SolrBackwardsCompatibleHttpClient(String baseURL) {
            super(baseURL);
        }

        /* Removing collection parameter since it causes wrong query URL */
        @Override
        protected HttpRequestBase createMethod(SolrRequest request, String collection) throws IOException,
                SolrServerException {
            return super.createMethod(request, null);
        }
    }

}
