package org.bspv.authorization.config.db;

import org.bspv.commons.config.listener.HsqldbServletContextListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DatabaseConfig {

    /**
     * Register a servlet context listener to start/stop a HSQLDB is no Spring
     * profile defined. Consider using {@link Conditional} for more complex
     * activation.
     * 
     * @return
     */
    @Bean
    @Profile("default")
    @ConditionalOnProperty(name = "spring.datasource.fallback.enabled", matchIfMissing = true)
    public HsqldbServletContextListener hsqldbServletContextListener() {
        return new HsqldbServletContextListener();
    }
}
