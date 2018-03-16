package org.bspv.authorization;

import org.bspv.security.annotation.EnableJwtFilter;
import org.bspv.security.annotation.EnableJwtServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

/**
 * 
 * FlywayAutoConfiguration is disabled. Flyway is only used with Maven profile
 * 'generate' and linked to Jooq class generation.
 */
//@ServletComponentScan

@EnableJwtFilter
@EnableJwtServer
@SpringBootApplication(exclude = { FlywayAutoConfiguration.class })
public class BspvAuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BspvAuthorizationApplication.class, args);
	}
}
