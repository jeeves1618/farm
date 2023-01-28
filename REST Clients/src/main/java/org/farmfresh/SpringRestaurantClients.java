package org.farmfresh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringRestaurantClients {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestaurantClients.class, args);
	}
}
