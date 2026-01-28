package com.timetracking.tracking.time_tracking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableKafka
public class TimeTrackingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeTrackingServiceApplication.class, args);
	}

}
