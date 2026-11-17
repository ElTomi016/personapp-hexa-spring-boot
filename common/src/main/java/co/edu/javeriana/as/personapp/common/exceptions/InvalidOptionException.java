package co.edu.javeriana.as.personapp.common.exceptions;

/** Se lanza cuando la opcion/motor recibido no es valido. */
public class InvalidOptionException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidOptionException(String mensaje) {
		super(mensaje);
	}

}
