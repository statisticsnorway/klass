package no.ssb.klass.api.config;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!mock-search")
public class OpenSearchConfig extends AbstractOpenSearchConfiguration {

    @Value("${opensearch.url}")
    private String opensearchUri;

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(opensearchUri.replace("http://", "").replace("https://", ""))
                .withConnectTimeout(20000)
                .withSocketTimeout(300000)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public OpenSearchRestTemplate opensearchRestTemplate() {
        return new OpenSearchRestTemplate(opensearchClient());
    }
}