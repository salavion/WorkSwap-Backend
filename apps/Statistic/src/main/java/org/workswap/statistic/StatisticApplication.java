package org.workswap.statistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "org.workswap")
@EnableScheduling
@EnableTransactionManagement
public class StatisticApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatisticApplication.class, args);
	}

}
