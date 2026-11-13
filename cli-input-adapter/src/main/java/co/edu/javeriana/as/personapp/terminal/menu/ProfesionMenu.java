package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.ProfessionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

public class ProfesionMenu extends CrudMenu {

	private final ProfessionInputAdapterCli professionInputAdapterCli;

	public ProfesionMenu(ProfessionInputAdapterCli professionInputAdapterCli) {
		this.professionInputAdapterCli = professionInputAdapterCli;
	}

	@Override
	protected String uno() {
		return "una profesion";
	}

	@Override
	protected String todos() {
		return "todas las profesiones";
	}

	@Override
	protected void crear(String db, ConsoleInput in) throws Exception {
		ProfesionModelCli model = new ProfesionModelCli();
		model.setId(in.integer("Id de la profesion", null, true, true));
		model.setNombre(in.text("Nombre", null, true));
		model.setDescripcion(in.text("Descripcion (opcional)", null, false));
		System.out.println("Profesion creada: " + professionInputAdapterCli.crear(db, model));
	}

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

	@Override
	protected void listar(String db, ConsoleInput in) throws Exception {
		imprimirLista(professionInputAdapterCli.historial(db));
	}

	@Override
	protected void buscar(String db, ConsoleInput in) throws Exception {
		Integer id = in.integer("Id de la profesion", null, true, true);
		System.out.println(professionInputAdapterCli.buscar(db, id));
	}

	@Override
	protected void contar(String db, ConsoleInput in) throws Exception {
		System.out.println("Total de profesiones: " + professionInputAdapterCli.contar(db));
	}
}
