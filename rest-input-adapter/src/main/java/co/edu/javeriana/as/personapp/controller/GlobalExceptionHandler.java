package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Traduce las excepciones a respuestas JSON con un unico cuerpo ({@link Response}).
 * Extiende ResponseEntityExceptionHandler para que los errores estandar de Spring
 * (JSON mal formado, id no numerico, metodo no soportado, etc.) conserven su codigo
 * 4xx y salgan con el mismo cuerpo, en lugar de caer en el manejador generico de 500.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(NoExistException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ApiResponse(responseCode = "404", description = "El registro pedido no existe", content = @Content(schema = @Schema(implementation = Response.class)))
	public Response handleNoExist(NoExistException ex) {
		log.info("Not found: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(InvalidOptionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ApiResponse(responseCode = "400", description = "Base de datos, identificador o datos de entrada invalidos", content = @Content(schema = @Schema(implementation = Response.class)))
	public Response handleInvalidOption(InvalidOptionException ex) {
		log.info("Bad request: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	/**
	 * Llave duplicada o clave foranea (DataIntegrityViolationException) y referencia
	 * a un registro inexistente (DataRetrievalFailureException, por ejemplo un
	 * telefono cuyo dueno no existe).
	 */
	@ExceptionHandler({ DataIntegrityViolationException.class, DataRetrievalFailureException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	@ApiResponse(responseCode = "409", description = "La operacion viola una restriccion de integridad (llave duplicada o referencia inexistente)", content = @Content(schema = @Schema(implementation = Response.class)))
	public Response handleDataIntegrity(DataAccessException ex) {
		log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
		return build(HttpStatus.CONFLICT,
				"The operation violates a data integrity constraint (duplicated key or missing reference)");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ApiResponse(responseCode = "500", description = "Error interno inesperado", content = @Content(schema = @Schema(implementation = Response.class)))
	public Response handleUnexpected(Exception ex) {
		log.error("Unexpected error", ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error, please try again later");
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return handleExceptionInternal(ex, build(status, "The request body is missing or is not valid JSON"), headers,
				status, request);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return handleExceptionInternal(ex,
				build(status, "Invalid value '" + ex.getValue() + "' for the path or query parameter"), headers,
				status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		Object responseBody = body != null ? body
				: build(status, status.is4xxClientError() ? ex.getMessage() : "Unexpected error, please try again later");
		return super.handleExceptionInternal(ex, responseBody, headers, status, request);
	}

	private Response build(HttpStatus status, String description) {
		return new Response(status.name(), description, LocalDateTime.now());
	}
}
