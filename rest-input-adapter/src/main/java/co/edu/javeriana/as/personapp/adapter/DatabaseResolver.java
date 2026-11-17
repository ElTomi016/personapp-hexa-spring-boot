package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;

/**
 * Utilidad sin estado: traduce el texto de la base ("maria" o "mongo", sin
 * importar mayusculas) a {@link DatabaseOption}.
 */
public final class DatabaseResolver {

	/** Clase utilitaria: no se instancia. */
	private DatabaseResolver() {
	}

	/** Texto 'maria'/'mongo' (sin importar mayusculas) -> DatabaseOption; error si no es valido. */
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

	/** Nombre legible del motor para mensajes. */
	public static String label(DatabaseOption option) {
		return option == DatabaseOption.MARIA ? "MariaDB" : "MongoDB";
	}
}
