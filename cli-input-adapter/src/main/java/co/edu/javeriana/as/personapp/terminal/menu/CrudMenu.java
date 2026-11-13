package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.List;
import java.util.Scanner;

import org.springframework.dao.DataAccessException;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import lombok.extern.slf4j.Slf4j;

/**
 * Flujo comun de los menus de cada modulo: elegir la base de datos (MariaDB o
 * MongoDB) y luego las seis operaciones crear, editar, eliminar, listar, buscar y
 * contar. Cada modulo implementa solo el pedido de datos y la llamada a su adaptador.
 * La base elegida es una variable local: el menu no guarda estado.
 */
@Slf4j
public abstract class CrudMenu {

	private static final int OPCION_REGRESAR_MODULOS = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
	private static final int OPCION_CREAR = 1;
	private static final int OPCION_EDITAR = 2;
	private static final int OPCION_ELIMINAR = 3;
	private static final int OPCION_VER_TODO = 4;
	private static final int OPCION_BUSCAR = 5;
	private static final int OPCION_CONTAR = 6;

	@FunctionalInterface
	protected interface Accion {
		void ejecutar() throws Exception;
	}

	/** Descripcion en singular con articulo, por ejemplo "una persona". */
	protected abstract String uno();

	/** Descripcion en plural con articulo, por ejemplo "todas las personas". */
	protected abstract String todos();

	protected abstract void crear(String db, ConsoleInput in) throws Exception;

	protected abstract void editar(String db, ConsoleInput in) throws Exception;

	protected abstract void eliminar(String db, ConsoleInput in) throws Exception;

	protected abstract void listar(String db, ConsoleInput in) throws Exception;

	protected abstract void buscar(String db, ConsoleInput in) throws Exception;

	protected abstract void contar(String db, ConsoleInput in) throws Exception;

	/** Opciones adicionales del modulo (por ejemplo, ver telefonos de una persona). */
	protected void mostrarOpcionesExtra() {
	}

	/** Devuelve la accion de una opcion adicional, o null si la opcion no existe. */
	protected Accion accionExtra(int opcion, String db, ConsoleInput in) {
		return null;
	}

	protected <T> void imprimirLista(List<T> items) {
		if (items.isEmpty()) {
			System.out.println("No hay registros.");
		}
		items.forEach(item -> System.out.println(item));
	}

	public void iniciarMenu(Scanner keyboard) {
		ConsoleInput in = new ConsoleInput(keyboard);
		boolean isValid = false;
		do {
			mostrarMenuMotorPersistencia();
			int opcion = in.readOption();
			switch (opcion) {
			case OPCION_REGRESAR_MODULOS:
				isValid = true;
				break;
			case PERSISTENCIA_MARIADB:
				menuOpciones("MARIA", in);
				break;
			case PERSISTENCIA_MONGODB:
				menuOpciones("MONGO", in);
				break;
			default:
				log.warn("La opcion elegida no es valida.");
			}
		} while (!isValid);
	}

	private void menuOpciones(String db, ConsoleInput in) {
		boolean isValid = false;
		do {
			mostrarMenuOpciones();
			int opcion = in.readOption();
			switch (opcion) {
			case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
				isValid = true;
				break;
			case OPCION_CREAR:
				ejecutar(() -> crear(db, in));
				break;
			case OPCION_EDITAR:
				ejecutar(() -> editar(db, in));
				break;
			case OPCION_ELIMINAR:
				ejecutar(() -> eliminar(db, in));
				break;
			case OPCION_VER_TODO:
				ejecutar(() -> listar(db, in));
				break;
			case OPCION_BUSCAR:
				ejecutar(() -> buscar(db, in));
				break;
			case OPCION_CONTAR:
				ejecutar(() -> contar(db, in));
				break;
			default:
				Accion extra = accionExtra(opcion, db, in);
				if (extra != null) {
					ejecutar(extra);
				} else {
					log.warn("La opcion elegida no es valida.");
				}
			}
		} while (!isValid);
	}

	/** Ejecuta una accion y convierte cualquier error en un mensaje, sin tumbar la aplicacion. */
	private void ejecutar(Accion accion) {
		try {
			accion.ejecutar();
		} catch (ConsoleInput.EndOfInputException e) {
			// La entrada se cerro: el siguiente menu lo detecta y regresa.
		} catch (NoExistException | InvalidOptionException e) {
			log.warn(e.getMessage());
		} catch (DataAccessException e) {
			log.warn("Error de base de datos: " + e.getMostSpecificCause().getMessage());
		} catch (Exception e) {
			log.warn("Error inesperado: " + e.getMessage());
		}
	}

	private void mostrarMenuOpciones() {
		System.out.println("----------------------");
		System.out.println(OPCION_CREAR + " para crear " + uno());
		System.out.println(OPCION_EDITAR + " para editar " + uno());
		System.out.println(OPCION_ELIMINAR + " para eliminar " + uno());
		System.out.println(OPCION_VER_TODO + " para ver " + todos());
		System.out.println(OPCION_BUSCAR + " para buscar " + uno() + " por su id");
		System.out.println(OPCION_CONTAR + " para contar " + todos());
		mostrarOpcionesExtra();
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("----------------------");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}
}
