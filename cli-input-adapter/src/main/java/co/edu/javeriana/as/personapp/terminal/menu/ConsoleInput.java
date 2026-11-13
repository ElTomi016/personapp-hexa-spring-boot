package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

/**
 * Lectura de datos por consola. Siempre lee lineas completas y las interpreta, de
 * modo que un texto invalido nunca deja la entrada bloqueada. Si la entrada se
 * cierra (fin de archivo), una opcion de menu equivale a 0 (regresar o salir) y
 * cualquier otro dato lanza {@link EndOfInputException}.
 */
@Slf4j
public class ConsoleInput {

	public static class EndOfInputException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public EndOfInputException() {
			super("Input closed");
		}
	}

	private final Scanner keyboard;

	public ConsoleInput(Scanner keyboard) {
		this.keyboard = keyboard;
	}

	private String readLine(String prompt) {
		System.out.print(prompt);
		System.out.flush();
		if (!keyboard.hasNextLine()) {
			System.out.println();
			throw new EndOfInputException();
		}
		String line = keyboard.nextLine();
		if (System.console() == null) {
			// Entrada redirigida (pruebas, contenedores sin tty): se repite lo leido para que la salida sea legible.
			System.out.println(line);
		}
		return line;
	}

	private String prompt(String label, Object current) {
		return label + (current != null ? " [" + current + "]" : "") + ": ";
	}

	/** Opcion numerica de un menu. Solo valida que sea un numero; el menu decide si esta en rango. */
	public int readOption() {
		while (true) {
			try {
				return Integer.parseInt(readLine("Ingrese una opcion: ").trim());
			} catch (NumberFormatException e) {
				log.warn("Solo se permiten numeros.");
			} catch (EndOfInputException e) {
				return 0;
			}
		}
	}

	public void editHint() {
		System.out.println("Enter conserva el valor actual; '-' deja vacio un campo opcional.");
	}

	public String text(String label, String current, boolean required) {
		while (true) {
			String line = readLine(prompt(label, current)).trim();
			if (line.isEmpty()) {
				if (current != null) {
					return current;
				}
				if (!required) {
					return null;
				}
				System.out.println("Este campo es obligatorio.");
				continue;
			}
			if (line.equals("-") && !required) {
				return null;
			}
			return line;
		}
	}

	public Integer integer(String label, Integer current, boolean required, boolean nonNegative) {
		while (true) {
			String line = readLine(prompt(label, current)).trim();
			if (line.isEmpty()) {
				if (current != null) {
					return current;
				}
				if (!required) {
					return null;
				}
				System.out.println("Este campo es obligatorio.");
				continue;
			}
			if (line.equals("-") && !required) {
				return null;
			}
			try {
				int number = Integer.parseInt(line);
				if (nonNegative && number < 0) {
					System.out.println("No puede ser negativo.");
					continue;
				}
				return number;
			} catch (NumberFormatException e) {
				System.out.println("Debe ser un numero entero.");
			}
		}
	}

	public LocalDate date(String label, LocalDate current) {
		while (true) {
			String line = readLine(prompt(label + " (yyyy-MM-dd, opcional)", current)).trim();
			if (line.isEmpty()) {
				return current;
			}
			if (line.equals("-")) {
				return null;
			}
			try {
				return LocalDate.parse(line);
			} catch (DateTimeParseException e) {
				System.out.println("Use el formato yyyy-MM-dd, por ejemplo 2020-05-20.");
			}
		}
	}

	/** Devuelve MALE, FEMALE u OTHER. Acepta M, F, O o el nombre completo, sin importar mayusculas. */
	public String gender(String label, String current) {
		while (true) {
			String line = readLine(prompt(label + " (M/F/O)", current)).trim().toUpperCase();
			if (line.isEmpty() && current != null) {
				return current;
			}
			switch (line) {
			case "M":
			case "MALE":
				return "MALE";
			case "F":
			case "FEMALE":
				return "FEMALE";
			case "O":
			case "OTHER":
				return "OTHER";
			default:
				System.out.println("Ingrese M, F u O.");
			}
		}
	}

	public boolean confirm(String question) {
		while (true) {
			String line = readLine(question + " (s/n): ").trim().toLowerCase();
			if (line.equals("s") || line.equals("si")) {
				return true;
			}
			if (line.equals("n") || line.equals("no")) {
				return false;
			}
			System.out.println("Responda s o n.");
		}
	}
}
