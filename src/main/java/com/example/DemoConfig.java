package com.example;

import com.gemstone.gemfire.cache.GemFireCache;
import com.gemstone.gemfire.cache.RegionAttributes;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.session.ExpiringSession;
import org.springframework.session.data.gemfire.config.annotation.web.http.EnableGemFireHttpSession;
import org.springframework.session.data.gemfire.config.annotation.web.http.GemFireHttpSessionConfiguration;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableGemFireHttpSession()
public class DemoConfig {

    static final String DEFAULT_GEMFIRE_LOG_LEVEL = "error";

    int intValue(Long value) {
        return value.intValue();
    }

    String applicationName() {
        return "DemoApplication";
    }

    String gemfireLogLevel() {
        return System.getProperty("gemfire.log-level", DEFAULT_GEMFIRE_LOG_LEVEL);
    }

    @Bean
    PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty("name", applicationName());
        gemfireProperties.setProperty("log-level", gemfireLogLevel());
        return gemfireProperties;
    }

    @Bean
    ClientCacheFactoryBean gemfireCache() {
        ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();

//        gemfireCache.setClose(true);
        gemfireCache.setProperties(gemfireProperties());
        gemfireCache.setPoolName("gemfirePool");
        return gemfireCache;
    }

    ConnectionEndpoint newConnectionEndpoint(String host, int port) {
        return new ConnectionEndpoint(host, port);
    }

    @Bean
    PoolFactoryBean gemfirePool() {
        PoolFactoryBean gemfirePool = new PoolFactoryBean();

        gemfirePool.setFreeConnectionTimeout(intValue(TimeUnit.SECONDS.toMillis(5)));
        gemfirePool.setIdleTimeout(TimeUnit.MINUTES.toMillis(2));
//        gemfirePool.setKeepAlive(false);
        gemfirePool.setMaxConnections(50);
        gemfirePool.setPingInterval(TimeUnit.SECONDS.toMillis(15));
        gemfirePool.setPrSingleHopEnabled(true);
        gemfirePool.setReadTimeout(intValue(TimeUnit.SECONDS.toMillis(5)));
        gemfirePool.setRetryAttempts(1);
//        gemfirePool.setSubscriptionEnabled(true);
        gemfirePool.setThreadLocalConnections(false);
        gemfirePool.setServers(Collections.singleton(newConnectionEndpoint("localhost", 12480)));

        return gemfirePool;
    }

    @Bean(name = GemFireHttpSessionConfiguration.DEFAULT_SPRING_SESSION_GEMFIRE_REGION_NAME)
    ClientRegionFactoryBean<Object, ExpiringSession> sessionRegion(GemFireCache gemfireCache,
                                                                   RegionAttributes<Object, ExpiringSession> sessionRegionAttributes) {

        System.out.println("HELLO FRO SESSION REGION!");

        ClientRegionFactoryBean<Object, ExpiringSession> sessionRegion =
            new ClientRegionFactoryBean<Object, ExpiringSession>();

        sessionRegion.setAttributes(sessionRegionAttributes);
        sessionRegion.setCache(gemfireCache);
        sessionRegion.setClose(false);
        sessionRegion.setPoolName("gemfirePool");
        sessionRegion.setShortcut(ClientRegionShortcut.PROXY);

        return sessionRegion;
    }
//
//
//    static final int GEMFIRE_CACHE_SERVER_PORT = 12480;
//    static final int MAX_CONNECTIONS = 50;
//
//    static final String DEFAULT_GEMFIRE_LOG_LEVEL = "error";
//    static final String GEMFIRE_CACHE_SERVER_HOST = "localhost";
//
//    String applicationName() {
//        return "SpringDataGemFireCacheClientSessionTests";
//    }
//
//    String gemfireLogLevel() {
//        return System.getProperty("gemfire.log.level", DEFAULT_GEMFIRE_LOG_LEVEL);
//    }
//
//    int intValue(Long value) {
//        return value.intValue();
//    }
//
//    ConnectionEndpoint newConnectionEndpoint(String host, int port) {
//        return new ConnectionEndpoint(host, port);
//    }
//
//    @Bean
//    PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }
//
//    @Bean
//    Properties gemfireProperties() {
//        Properties gemfireProperties = new Properties();
//        gemfireProperties.setProperty("name", applicationName());
//        gemfireProperties.setProperty("log-level", gemfireLogLevel());
//        return gemfireProperties;
//    }
//
//    @Bean
//    ClientCacheFactoryBean gemfireCache() {
//        ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();
//
//        gemfireCache.setClose(true);
//        gemfireCache.setProperties(gemfireProperties());
//
//        return gemfireCache;
//    }
//
//    @Bean
//    PoolFactoryBean gemfirePool() {
//        PoolFactoryBean gemfirePool = new PoolFactoryBean();
//
//        gemfirePool.setFreeConnectionTimeout(intValue(TimeUnit.SECONDS.toMillis(5)));
//        gemfirePool.setIdleTimeout(TimeUnit.MINUTES.toMillis(2));
//        gemfirePool.setKeepAlive(false);
//        gemfirePool.setMaxConnections(50);
//        gemfirePool.setPingInterval(TimeUnit.SECONDS.toMillis(15));
//        gemfirePool.setReadTimeout(intValue(TimeUnit.SECONDS.toMillis(20)));
//        gemfirePool.setRetryAttempts(1);
//        gemfirePool.setSubscriptionEnabled(true);
//        gemfirePool.addServers(newConnectionEndpoint(GEMFIRE_CACHE_SERVER_HOST, GEMFIRE_CACHE_SERVER_PORT));
//
//        return gemfirePool;
//    }
}
