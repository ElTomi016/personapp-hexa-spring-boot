package co.edu.javeriana.as.personapp.common.exceptions;

/** Se lanza cuando el registro buscado no existe (REST la traduce a 404). */
public class NoExistException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoExistException(String mensaje) {
		super(mensaje);
	}

}
