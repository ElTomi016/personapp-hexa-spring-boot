package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.ProfessionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

/** [Integrante B] Menu CLI de Profesion. */
public class ProfesionMenu extends CrudMenu {

	private final ProfessionInputAdapterCli professionInputAdapterCli;

	public ProfesionMenu(ProfessionInputAdapterCli professionInputAdapterCli) {
		this.professionInputAdapterCli = professionInputAdapterCli;
	}

	/** Texto en singular usado en los mensajes del menu. */
	@Override
	protected String uno() {
		return "una profesion";
	}

	/** Texto en plural usado en los mensajes del menu. */
	@Override
	protected String todos() {
		return "todas las profesiones";
	}

	/** Opcion 1: pide los datos por consola y crea el registro. */
	@Override
	protected void crear(String db, ConsoleInput in) throws Exception {
		ProfesionModelCli model = new ProfesionModelCli();
		model.setId(in.integer("Id de la profesion", null, true, true));
		model.setNombre(in.text("Nombre", null, true));
		model.setDescripcion(in.text("Descripcion (opcional)", null, false));
		System.out.println("Profesion creada: " + professionInputAdapterCli.crear(db, model));
	}

	/** Opcion 2: pide la llave y los nuevos datos; Enter conserva el valor actual. */
	@Override
	protected void editar(String db, ConsoleInput in) throws Exception {
		Integer id = in.integer("Id de la profesion a editar", null, true, true);
		ProfesionModelCli actual = professionInputAdapterCli.buscar(db, id);
		in.editHint();
		ProfesionModelCli model = new ProfesionModelCli();
		model.setNombre(in.text("Nombre", actual.getNombre(), true));
		model.setDescripcion(in.text("Descripcion (opcional)", actual.getDescripcion(), false));
		System.out.println("Profesion editada: " + professionInputAdapterCli.editar(db, id, model));
	}

	/** Opcion 3: pide la llave, confirma y elimina. */
	@Override
	protected void eliminar(String db, ConsoleInput in) throws Exception {
		Integer id = in.integer("Id de la profesion a eliminar", null, true, true);
		ProfesionModelCli actual = professionInputAdapterCli.buscar(db, id);
		System.out.println(actual);
		if (!in.confirm("Eliminar tambien los estudios de esta profesion. Confirma")) {
			System.out.println("Operacion cancelada.");
			return;
		}
		if (Boolean.TRUE.equals(professionInputAdapterCli.eliminar(db, id))) {
			System.out.println("Profesion " + id + " eliminada.");
		} else {
			System.out.println("No se pudo eliminar la profesion " + id + ".");
		}
	}

	/** Opcion 4: imprime todos los registros. */
	@Override
	protected void listar(String db, ConsoleInput in) throws Exception {
		imprimirLista(professionInputAdapterCli.historial(db));
	}

	/** Opcion 5: busca e imprime un registro por su llave. */
	@Override
	protected void buscar(String db, ConsoleInput in) throws Exception {
		Integer id = in.integer("Id de la profesion", null, true, true);
		System.out.println(professionInputAdapterCli.buscar(db, id));
	}

	/** Opcion 6: imprime la cantidad de registros. */
	@Override
	protected void contar(String db, ConsoleInput in) throws Exception {
		System.out.println("Total de profesiones: " + professionInputAdapterCli.contar(db));
	}
}
