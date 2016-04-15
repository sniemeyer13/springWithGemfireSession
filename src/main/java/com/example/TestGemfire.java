package com.example;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.GemfireOperations;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.GemfireConstants;
import org.springframework.session.ExpiringSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.gemfire.GemFireOperationsSessionRepository;
import org.springframework.session.data.gemfire.config.annotation.web.http.GemFireHttpSessionConfiguration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class TestGemfire {

    protected static final int DEFAULT_CACHE_SERVER_PORT = 12480;
    protected static final int DEFAULT_CACHE_LOCATOR_PORT = 11235;
    protected static final int MAX_CONNECTIONS = 50;

    private static final Object LOCK = new Object();

    protected static final String DEFAULT_CACHE_SERVER_HOST = "localhost";
    protected static final String DEFAULT_GEMFIRE_LOG_LEVEL = "warning";
    protected static final String GEMFIRE_POOL_NAME = GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME;
    protected static final String GEMFIRE_REGION_NAME =
        GemFireHttpSessionConfiguration.DEFAULT_SPRING_SESSION_GEMFIRE_REGION_NAME;
    private final Pool gemfirePool;
    private final Region<Object, ExpiringSession> sessionRegion;

    //    private static ClientCache gemfireCache;

    private ClientCache gemfireCache;

    @Autowired
    public TestGemfire(ClientCacheFactoryBean gemfireCache,
                       PoolFactoryBean gemfirePool,
                       @Qualifier(GemFireHttpSessionConfiguration.DEFAULT_SPRING_SESSION_GEMFIRE_REGION_NAME) ClientRegionFactoryBean<Object, ExpiringSession> clientRegionFactoryBean
                       ) throws Exception {
        this.gemfireCache = (ClientCache) gemfireCache.getObject();
        this.gemfirePool = gemfirePool.getObject();

        this.sessionRegion = clientRegionFactoryBean.getObject();
        sessionRepository = sessionRepository(gemfireOperations(this.sessionRegion));
    }

    private static SessionRepository<ExpiringSession> sessionRepository = null;

    static GemfireOperations gemfireOperations(Region<Object, ExpiringSession> sessionRegion) {
        return new GemfireTemplate(sessionRegion);
    }

    static SessionRepository<ExpiringSession> sessionRepository(GemfireOperations gemfireOperations) {
        return new GemFireOperationsSessionRepository(gemfireOperations);
    }

    ExpiringSession load(Object sessionId) {
        return sessionRepository.getSession(sessionId.toString());
    }

    ExpiringSession loadDirect(Object sessionId) {
        return gemfireCache.<Object, ExpiringSession>getRegion(GEMFIRE_REGION_NAME).get(sessionId);
    }

    ExpiringSession newSession() {
        return sessionRepository.createSession();
    }

    ExpiringSession save(ExpiringSession session) {
        sessionRepository.save(session);
        return session;
    }

    ExpiringSession touch(ExpiringSession session) {
        session.setLastAccessedTime(System.currentTimeMillis());
        return session;
    }

    interface Condition {
        boolean evaluate();
    }

    void waitOnConditionForDuration(Condition condition, long duration) {
        final long timeout = (System.currentTimeMillis() + duration);

        boolean interrupted = false;

        try {
            // wait uninterrupted...
            while (!condition.evaluate() && System.currentTimeMillis() < timeout) {
                synchronized (LOCK) {
                    try {
                        TimeUnit.MILLISECONDS.timedWait(LOCK, 500);
                    }
                    catch (InterruptedException ignore) {
                        interrupted = true;
                    }
                }
            }
        }
        finally {
            // but, if we were interrupted, reset the interrupt!
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    String sessionId = null;

    public Session interactWithGemfire() {

        ExpiringSession expected = sessionId != null ? load(sessionId) : null;
        if (expected == null) {
            expected = save(touch(newSession()));
            sessionId = expected.getId();
        }

        expected.setAttribute("AccessedAt", OffsetDateTime.now());
        return expected;
    }
}
