package com.example.client;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.session.data.gemfire.config.annotation.web.http.EnableGemFireHttpSession;

@EnableGemFireHttpSession
public class DemoConfig {

    static final int MAX_CONNECTIONS = 50;
    static final String DEFAULT_GEMFIRE_LOG_LEVEL = "config";

    String applicationName() {
        return "DemoApplication";
    }

    String gemfireLogLevel() {
        return System.getProperty("gemfire.log-level", DEFAULT_GEMFIRE_LOG_LEVEL);
    }

    int intValue(Long value) {
        return value.intValue();
    }

    ConnectionEndpoint newConnectionEndpoint(String host, int port) {
        return new ConnectionEndpoint(host, port);
    }

    Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty("name", applicationName());
        gemfireProperties.setProperty("log-level", gemfireLogLevel());
        return gemfireProperties;
    }

    @Bean
    ClientCacheFactoryBean gemfireCache() {
        ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();

        gemfireCache.setClose(true);
        gemfireCache.setProperties(gemfireProperties());

        return gemfireCache;
    }

    @Bean
    PoolFactoryBean gemfirePool(@Value("${gemfire.cache.server.host:localhost}") String host,
            @Value("${gemfire.cache.server.port:12480}") int port) {

        PoolFactoryBean gemfirePool = new PoolFactoryBean();

        gemfirePool.setMaxConnections(MAX_CONNECTIONS);
        gemfirePool.setPingInterval(TimeUnit.SECONDS.toMillis(5));
        gemfirePool.setRetryAttempts(1);
        gemfirePool.setSubscriptionEnabled(true);
        gemfirePool.addServers(newConnectionEndpoint(host, port));

        return gemfirePool;
    }

    @Bean
    PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
