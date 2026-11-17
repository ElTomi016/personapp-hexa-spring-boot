package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;

/**
 * Validaciones de los datos de entrada del API REST (todos llegan como texto).
 * Cualquier valor invalido termina en {@link InvalidOptionException}.
 */
public final class RequestParser {

	/** Clase utilitaria: no se instancia. */
	private RequestParser() {
	}

	/** Exige que el cuerpo del request exista. */
	public static void requireBody(Object body) throws InvalidOptionException {
		if (body == null) {
			throw new InvalidOptionException("The request body is required");
		}
	}

	/** Texto obligatorio, sin espacios sobrantes. */
	public static String requiredText(String field, String value) throws InvalidOptionException {
		if (value == null || value.trim().isEmpty()) {
			throw new InvalidOptionException("The field '" + field + "' is required");
		}
		return value.trim();
	}

	/** Texto opcional: null si viene vacio. */
	public static String optionalText(String value) {
		return value == null || value.trim().isEmpty() ? null : value.trim();
	}

	/** Entero obligatorio; error si no es numero. */
	public static Integer requiredInteger(String field, String value) throws InvalidOptionException {
		String text = requiredText(field, value);
		try {
			return Integer.valueOf(text);
		} catch (NumberFormatException e) {
			throw new InvalidOptionException("The field '" + field + "' must be an integer: " + value);
		}
	}

	/** Entero opcional >= 0; null si no viene. */
	public static Integer optionalNonNegativeInteger(String field, String value) throws InvalidOptionException {
		if (optionalText(value) == null) {
			return null;
		}
		Integer number = requiredInteger(field, value);
		if (number < 0) {
			throw new InvalidOptionException("The field '" + field + "' cannot be negative: " + value);
		}
		return number;
	}

	/** Fecha opcional ISO (yyyy-MM-dd); null si no viene. */
	public static LocalDate optionalDate(String field, String value) throws InvalidOptionException {
		if (optionalText(value) == null) {
			return null;
		}
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			throw new InvalidOptionException("The field '" + field + "' must use the format yyyy-MM-dd: " + value);
		}
	}
}
