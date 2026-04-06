package at.fhv.blueroute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BlueRouteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueRouteApplication.class, args);
	}

}
