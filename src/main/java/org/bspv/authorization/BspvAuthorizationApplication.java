package org.bspv.authorization;

import org.bspv.security.annotation.EnableJwtFilter;
import org.bspv.security.annotation.EnableJwtServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * FlywayAutoConfiguration is disabled. Flyway is only used with Maven profile
 * 'generate' and linked to Jooq class generation.
 */
@EnableJwtFilter
@EnableJwtServer
@EnableWebSecurity
@ServletComponentScan
@SpringBootApplication(exclude = { FlywayAutoConfiguration.class, SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class })
public class BspvAuthorizationApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BspvAuthorizationApplication.class, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.boot.web.support.SpringBootServletInitializer#run(org.
     * springframework.boot.SpringApplication)
     */
    @Override
    protected WebApplicationContext run(SpringApplication application) {
        return super.run(application);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.boot.context.web.SpringBootServletInitializer#
     * configure(org.springframework.boot.builder.SpringApplicationBuilder)
     */
    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(BspvAuthorizationApplication.class).initializers();
    }

}
