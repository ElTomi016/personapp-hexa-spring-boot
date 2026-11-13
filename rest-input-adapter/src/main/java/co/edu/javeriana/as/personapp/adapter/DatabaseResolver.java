package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;

/**
 * Utilidad sin estado: traduce el texto de la base ("maria" o "mongo", sin
 * importar mayusculas) a {@link DatabaseOption}.
 */
public final class DatabaseResolver {

	private DatabaseResolver() {
	}

	public static DatabaseOption parse(String database) throws InvalidOptionException {
		if (database != null) {
			for (DatabaseOption option : DatabaseOption.values()) {
				if (option.name().equalsIgnoreCase(database.trim())) {
					return option;
				}
			}
		}
		throw new InvalidOptionException("Invalid database option: " + database);
	}

	public static String label(DatabaseOption option) {
		return option == DatabaseOption.MARIA ? "MariaDB" : "MongoDB";
	}
}
