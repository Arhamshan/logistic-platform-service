package com.logistic.platform;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class LogisticPlatformServiceApplication {

	public static void main(String[] args) {
		// Force AWT Headless Mode before Spring boots
		System.setProperty("java.awt.headless", "true");

		SpringApplication.run(LogisticPlatformServiceApplication.class, args);
	}

}
