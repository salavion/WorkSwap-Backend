package org.workswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication(
	scanBasePackages = {
		"org.workswap", 
		"org.salavion.security"
	},
	exclude = DataSourceAutoConfiguration.class
)
@EnableScheduling
@EnableTransactionManagement
public class WorkSwapApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkSwapApiApplication.class, args);
	}
}
