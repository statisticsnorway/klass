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

    @Value("${spring.elasticsearch.uris}")
    private String opensearchUri;

    @Value("${open.search.username}")
    private String username;

    @Value("${open.search.password}")
    private String password;

    @Value("${open.search.uri}")
    private String uri;

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(uri.replace("http://", "").replace("https://", ""))
                .withBasicAuth(username, password)
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