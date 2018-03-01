package org.bspv.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

/**
 * 
 * FlywayAutoConfiguration is disabled. Flyway is only used with Maven profile
 * 'generate' and linked to Jooq class generation.
 */
//@ServletComponentScan
@SpringBootApplication(exclude = { FlywayAutoConfiguration.class })
public class BspvAuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BspvAuthorizationApplication.class, args);
	}
}
