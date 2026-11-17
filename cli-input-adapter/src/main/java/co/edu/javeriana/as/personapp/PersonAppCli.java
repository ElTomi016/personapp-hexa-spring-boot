package co.edu.javeriana.as.personapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import co.edu.javeriana.as.personapp.terminal.menu.MenuPrincipal;
import lombok.extern.slf4j.Slf4j;

/** [Integrante B] Punto de entrada CLI: arranca Spring y abre el menu principal. */
@Slf4j
@SpringBootApplication
public class PersonAppCli implements CommandLineRunner {
	
	@Autowired
	private MenuPrincipal menuPrincipal;

	/** Arranca el contexto de Spring (CLI). */
	public static void main(String[] args) {
		log.info("Starting PersonAppCli ...");
		SpringApplication.run(PersonAppCli.class, args);
		log.info("Started PersonAppCli OK");
	}

	/** Se ejecuta al iniciar: abre el menu principal. */
	@Override
	public void run(String... args) throws Exception {
		log.info("EXECUTING : command line runner");
		menuPrincipal.inicio();
		log.info("FINISHED : command line runner");
	}

}
