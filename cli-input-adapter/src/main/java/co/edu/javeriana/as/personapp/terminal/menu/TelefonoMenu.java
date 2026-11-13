package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.terminal.adapter.PhoneInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

public class TelefonoMenu extends CrudMenu {

	private final PhoneInputAdapterCli phoneInputAdapterCli;

	public TelefonoMenu(PhoneInputAdapterCli phoneInputAdapterCli) {
		this.phoneInputAdapterCli = phoneInputAdapterCli;
	}

	@Override
	protected String uno() {
		return "un telefono";
	}

	@Override
	protected String todos() {
		return "todos los telefonos";
	}

	@Override
	protected void crear(String db, ConsoleInput in) throws Exception {
		TelefonoModelCli model = new TelefonoModelCli();
		model.setNumero(in.text("Numero", null, true));
		model.setOperador(in.text("Operador", null, true));
		model.setDuenio(in.integer("Cedula del duenio", null, true, true));
		System.out.println("Telefono creado: " + phoneInputAdapterCli.crear(db, model));
	}

	@Override
	protected void editar(String db, ConsoleInput in) throws Exception {
		String numero = in.text("Numero del telefono a editar", null, true);
		TelefonoModelCli actual = phoneInputAdapterCli.buscar(db, numero);
		in.editHint();
		TelefonoModelCli model = new TelefonoModelCli();
		model.setOperador(in.text("Operador", actual.getOperador(), true));
		model.setDuenio(in.integer("Cedula del duenio", actual.getDuenio(), true, true));
		System.out.println("Telefono editado: " + phoneInputAdapterCli.editar(db, numero, model));
	}

	@Override
	protected void eliminar(String db, ConsoleInput in) throws Exception {
		String numero = in.text("Numero del telefono a eliminar", null, true);
		System.out.println(phoneInputAdapterCli.buscar(db, numero));
		if (!in.confirm("Confirma")) {
			System.out.println("Operacion cancelada.");
			return;
		}
		if (Boolean.TRUE.equals(phoneInputAdapterCli.eliminar(db, numero))) {
			System.out.println("Telefono " + numero + " eliminado.");
		} else {
			System.out.println("No se pudo eliminar el telefono " + numero + ".");
		}
	}

	@Override
	protected void listar(String db, ConsoleInput in) throws Exception {
		imprimirLista(phoneInputAdapterCli.historial(db));
	}

	@Override
	protected void buscar(String db, ConsoleInput in) throws Exception {
		String numero = in.text("Numero del telefono", null, true);
		System.out.println(phoneInputAdapterCli.buscar(db, numero));
	}

	@Override
	protected void contar(String db, ConsoleInput in) throws Exception {
		System.out.println("Total de telefonos: " + phoneInputAdapterCli.contar(db));
	}
}
