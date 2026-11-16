package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.PhoneInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfessionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.StudyInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MenuPrincipal {
	
	//Beans
	@Autowired
	private PersonaInputAdapterCli personaInputAdapterCli;

	@Autowired
	private ProfessionInputAdapterCli professionInputAdapterCli;

	@Autowired
	private PhoneInputAdapterCli phoneInputAdapterCli;

	@Autowired
	private StudyInputAdapterCli studyInputAdapterCli;

	private static final int SALIR = 0;
	private static final int MODULO_PERSONA = 1;
	private static final int MODULO_PROFESION = 2;
	private static final int MODULO_TELEFONO = 3;
	private static final int MODULO_ESTUDIO = 4;

	private final Scanner keyboard;

    public MenuPrincipal() {
        this.keyboard = new Scanner(System.in);
    }

	public void inicio() {
		
		//Menus
		PersonaMenu personaMenu = new PersonaMenu(personaInputAdapterCli);
		ProfesionMenu profesionMenu = new ProfesionMenu(professionInputAdapterCli);
		TelefonoMenu telefonoMenu = new TelefonoMenu(phoneInputAdapterCli);
		EstudioMenu estudioMenu = new EstudioMenu(studyInputAdapterCli);
		ConsoleInput input = new ConsoleInput(keyboard);

		boolean isValid = false;
		do {
			mostrarMenu();
			int opcion = input.readOption();
			switch (opcion) {
			case SALIR:
				isValid = true;
				break;
			case MODULO_PERSONA:
				personaMenu.iniciarMenu(keyboard);
				break;
			case MODULO_PROFESION:
				profesionMenu.iniciarMenu(keyboard);
				break;
			case MODULO_TELEFONO:
				telefonoMenu.iniciarMenu(keyboard);
				break;
			case MODULO_ESTUDIO:
				estudioMenu.iniciarMenu(keyboard);
				break;
			default:
				log.warn("La opcion elegida no es valida.");
			}

		} while (!isValid);
		keyboard.close();
	}

	private void mostrarMenu() {
		System.out.println("----------------------");
		System.out.println(MODULO_PERSONA + " para trabajar con el Modulo de Personas");
		System.out.println(MODULO_PROFESION + " para trabajar con el Modulo de Profesiones");
		System.out.println(MODULO_TELEFONO + " para trabajar con el Modulo de Telefonos");
		System.out.println(MODULO_ESTUDIO + " para trabajar con el Modulo de Estudios");
		System.out.println(SALIR + " para Salir");
	}

}
