package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.StudyInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

public class EstudioMenu extends CrudMenu {

	private final StudyInputAdapterCli studyInputAdapterCli;

	public EstudioMenu(StudyInputAdapterCli studyInputAdapterCli) {
		this.studyInputAdapterCli = studyInputAdapterCli;
	}

	@Override
	protected String uno() {
		return "un estudio";
	}

	@Override
	protected String todos() {
		return "todos los estudios";
	}

	@Override
	protected void crear(String db, ConsoleInput in) throws Exception {
		EstudioModelCli model = new EstudioModelCli();
		model.setCc(in.integer("Cedula de la persona", null, true, true));
		model.setIdProfesion(in.integer("Id de la profesion", null, true, true));
		model.setFecha(in.date("Fecha de graduacion", null));
		model.setUniversidad(in.text("Universidad (opcional)", null, false));
		System.out.println("Estudio creado: " + studyInputAdapterCli.crear(db, model));
	}

	@Override
	protected void editar(String db, ConsoleInput in) throws Exception {
		Integer cc = in.integer("Cedula de la persona del estudio a editar", null, true, true);
		Integer idProfesion = in.integer("Id de la profesion del estudio a editar", null, true, true);
		EstudioModelCli actual = studyInputAdapterCli.buscar(db, cc, idProfesion);
		in.editHint();
		EstudioModelCli model = new EstudioModelCli();
		model.setFecha(in.date("Fecha de graduacion", actual.getFecha()));
		model.setUniversidad(in.text("Universidad (opcional)", actual.getUniversidad(), false));
		System.out.println("Estudio editado: " + studyInputAdapterCli.editar(db, cc, idProfesion, model));
	}

	@Override
	protected void eliminar(String db, ConsoleInput in) throws Exception {
		Integer cc = in.integer("Cedula de la persona del estudio a eliminar", null, true, true);
		Integer idProfesion = in.integer("Id de la profesion del estudio a eliminar", null, true, true);
		System.out.println(studyInputAdapterCli.buscar(db, cc, idProfesion));
		if (!in.confirm("Confirma")) {
			System.out.println("Operacion cancelada.");
			return;
		}
		if (Boolean.TRUE.equals(studyInputAdapterCli.eliminar(db, cc, idProfesion))) {
			System.out.println("Estudio de " + cc + " en la profesion " + idProfesion + " eliminado.");
		} else {
			System.out.println("No se pudo eliminar el estudio.");
		}
	}

	@Override
	protected void listar(String db, ConsoleInput in) throws Exception {
		imprimirLista(studyInputAdapterCli.historial(db));
	}

	@Override
	protected void buscar(String db, ConsoleInput in) throws Exception {
		Integer cc = in.integer("Cedula de la persona", null, true, true);
		Integer idProfesion = in.integer("Id de la profesion", null, true, true);
		System.out.println(studyInputAdapterCli.buscar(db, cc, idProfesion));
	}

	@Override
	protected void contar(String db, ConsoleInput in) throws Exception {
		System.out.println("Total de estudios: " + studyInputAdapterCli.contar(db));
	}
}
