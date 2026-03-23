package io.github.nguyennhatquang.fashion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class FashionEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FashionEcommerceApplication.class, args);
	}

}
