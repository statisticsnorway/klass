package no.ssb.klass.search;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

/**
 * Configuration class that sets up a lazy-loading data source proxy to improve performance by
 * deferring physical database connections until they are actually needed. This optimization is
 * particularly useful in scenarios where database operations don't always occur during request
 * processing.
 */
@AutoConfiguration(
        after = {DataSourceAutoConfiguration.class},
        before = {HibernateJpaAutoConfiguration.class})
class LazyConnectionDataSourceProxyConfig {

    @Bean
    @Primary
    DataSource lazyConnectionDataSourceProxy(DataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }
}
