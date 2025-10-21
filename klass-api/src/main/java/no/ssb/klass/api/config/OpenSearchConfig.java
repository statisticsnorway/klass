package no.ssb.klass.api.config;

import no.ssb.klass.api.services.SearchServiceImpl;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.opensearch.data.client.orhlc.RestClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@Profile("!mock-search")
public class OpenSearchConfig extends AbstractOpenSearchConfiguration {
    private static final Logger log = LoggerFactory.getLogger(OpenSearchConfig.class);
    @Value("${opensearch.url}")
    private String opensearchUri;

    @Value("${opensearch.username}")
    private String username;

    @Value("${opensearch.password}")
    private String password;

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(opensearchUri.replace("http://", "").replace("https://", ""))
                .usingSsl()
                .withBasicAuth(username, password)
                .withConnectTimeout(Duration.ofSeconds(10))
                .withSocketTimeout(Duration.ofSeconds(5))
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public OpenSearchRestTemplate opensearchRestTemplate() {
        return new OpenSearchRestTemplate(opensearchClient());
    }
}