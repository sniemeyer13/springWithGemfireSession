package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.session.data.gemfire.config.annotation.web.http.EnableGemFireHttpSession;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@EnableGemFireHttpSession
public class DemoConfig {
    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    Properties gemfireProperties() {
        Properties properties = new Properties();
        properties.setProperty("log-level", "warning");
        return properties;
    }

    @Bean
    PoolFactoryBean gemfirePool() {

        PoolFactoryBean poolFactory = new PoolFactoryBean();

        poolFactory.setFreeConnectionTimeout(5000); // 5 seconds
        poolFactory.setKeepAlive(false);
        poolFactory.setMaxConnections(50);
        poolFactory.setPingInterval(TimeUnit.SECONDS.toMillis(5));
        poolFactory.setReadTimeout(2000); // 2 seconds
        poolFactory.setRetryAttempts(2);
        poolFactory.setSubscriptionEnabled(true);
        poolFactory.setThreadLocalConnections(false);

        poolFactory.setServers(Collections.singletonList(new ConnectionEndpoint("localhost", 12480)));

        return poolFactory;
    }

    @Bean
    ClientCacheFactoryBean gemfireCache(PoolFactoryBean poolFactoryBean) {
        ClientCacheFactoryBean clientCacheFactory = new ClientCacheFactoryBean();

        clientCacheFactory.setClose(true);
        clientCacheFactory.setProperties(gemfireProperties());

        return clientCacheFactory;
    }
}
