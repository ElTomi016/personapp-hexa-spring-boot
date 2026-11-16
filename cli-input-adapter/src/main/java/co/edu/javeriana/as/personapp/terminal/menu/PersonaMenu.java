package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

public class PersonaMenu extends CrudMenu {

	private static final int OPCION_VER_TELEFONOS = 7;
	private static final int OPCION_VER_ESTUDIOS = 8;

	private final PersonaInputAdapterCli personaInputAdapterCli;

	public PersonaMenu(PersonaInputAdapterCli personaInputAdapterCli) {
		this.personaInputAdapterCli = personaInputAdapterCli;
	}

	@Override
	protected String uno() {
		return "una persona";
	}

	@Override
	protected String todos() {
		return "todas las personas";
	}

	@Override
	protected void crear(String db, ConsoleInput in) throws Exception {
		PersonaModelCli model = new PersonaModelCli();
		model.setCc(in.integer("Cedula (cc)", null, true, true));
		model.setNombre(in.text("Nombre", null, true));
		model.setApellido(in.text("Apellido", null, true));
		model.setGenero(in.gender("Genero", null));
		model.setEdad(in.integer("Edad (opcional)", null, false, true));
		System.out.println("Persona creada: " + personaInputAdapterCli.crear(db, model));
	}

	@Override
	protected void editar(String db, ConsoleInput in) throws Exception {
		Integer cc = in.integer("Cedula de la persona a editar", null, true, true);
		PersonaModelCli actual = personaInputAdapterCli.buscar(db, cc);
		in.editHint();
		PersonaModelCli model = new PersonaModelCli();
		model.setNombre(in.text("Nombre", actual.getNombre(), true));
		model.setApellido(in.text("Apellido", actual.getApellido(), true));
		model.setGenero(in.gender("Genero", actual.getGenero()));
		model.setEdad(in.integer("Edad (opcional)", actual.getEdad(), false, true));
		System.out.println("Persona editada: " + personaInputAdapterCli.editar(db, cc, model));
	}

	@Override
	protected void eliminar(String db, ConsoleInput in) throws Exception {
		Integer cc = in.integer("Cedula de la persona a eliminar", null, true, true);
		PersonaModelCli actual = personaInputAdapterCli.buscar(db, cc);
		System.out.println(actual);
		if (!in.confirm("Eliminar tambien sus telefonos y estudios. Confirma")) {
			System.out.println("Operacion cancelada.");
			return;
		}
		if (Boolean.TRUE.equals(personaInputAdapterCli.eliminar(db, cc))) {
			System.out.println("Persona " + cc + " eliminada.");
		} else {
			System.out.println("No se pudo eliminar la persona " + cc + ".");
		}
	}

	@Override
	protected void listar(String db, ConsoleInput in) throws Exception {
		imprimirLista(personaInputAdapterCli.historial(db));
	}

	@Override
	protected void buscar(String db, ConsoleInput in) throws Exception {
		Integer cc = in.integer("Cedula de la persona", null, true, true);
		System.out.println(personaInputAdapterCli.buscar(db, cc));
	}

	@Override
	protected void contar(String db, ConsoleInput in) throws Exception {
		System.out.println("Total de personas: " + personaInputAdapterCli.contar(db));
	}

	@Override
	protected void mostrarOpcionesExtra() {
		System.out.println(OPCION_VER_TELEFONOS + " para ver los telefonos de una persona");
		System.out.println(OPCION_VER_ESTUDIOS + " para ver los estudios de una persona");
	}

	@Override
	protected Accion accionExtra(int opcion, String db, ConsoleInput in) {
		switch (opcion) {
		case OPCION_VER_TELEFONOS:
			return () -> imprimirLista(personaInputAdapterCli.telefonos(db,
					in.integer("Cedula de la persona", null, true, true)));
		case OPCION_VER_ESTUDIOS:
			return () -> imprimirLista(personaInputAdapterCli.estudios(db,
					in.integer("Cedula de la persona", null, true, true)));
		default:
			return null;
		}
	}
}
