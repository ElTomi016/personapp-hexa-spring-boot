package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;

/**
 * Validaciones de los datos de entrada del API REST (todos llegan como texto).
 * Cualquier valor invalido termina en {@link InvalidOptionException}.
 */
public final class RequestParser {

	private RequestParser() {
	}

	public static void requireBody(Object body) throws InvalidOptionException {
		if (body == null) {
			throw new InvalidOptionException("The request body is required");
		}
	}

	public static String requiredText(String field, String value) throws InvalidOptionException {
		if (value == null || value.trim().isEmpty()) {
			throw new InvalidOptionException("The field '" + field + "' is required");
		}
		return value.trim();
	}

	public static String optionalText(String value) {
		return value == null || value.trim().isEmpty() ? null : value.trim();
	}

	public static Integer requiredInteger(String field, String value) throws InvalidOptionException {
		String text = requiredText(field, value);
		try {
			return Integer.valueOf(text);
		} catch (NumberFormatException e) {
			throw new InvalidOptionException("The field '" + field + "' must be an integer: " + value);
		}
	}

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
