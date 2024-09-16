package it.cittalaggiu.gestioneprodotti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestioneprodottiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestioneprodottiApplication.class, args);
	}

}
