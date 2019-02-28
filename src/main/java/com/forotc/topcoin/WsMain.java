package com.forotc.topcoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WsMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(WsMain.class);
	private static final String PID_FILE_NAME = "ws.pid";
	public static ApplicationContext ac;

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(WsMain.class);
		app.addListeners(new ApplicationPidFileWriter(PID_FILE_NAME));
		app.setRegisterShutdownHook(true);
		ac = app.run(args);

		for (String str : ac.getEnvironment().getActiveProfiles()) {
			LOGGER.info(str);
		}
		LOGGER.info("Boot Server started.");
	}
}
