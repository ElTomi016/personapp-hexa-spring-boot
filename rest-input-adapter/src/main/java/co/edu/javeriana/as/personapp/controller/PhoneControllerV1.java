package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PhoneInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/telefono")
@Tag(name = "Telefono", description = "Operaciones sobre telefonos en MariaDB o MongoDB")
public class PhoneControllerV1 {

	@Autowired
	private PhoneInputAdapterRest phoneInputAdapterRest;

	@Operation(summary = "Listar telefonos", description = "Devuelve todos los telefonos de la base indicada.")
	@ApiResponse(responseCode = "200", description = "Lista de telefonos")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PhoneResponse> telefonos(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into telefonos REST API");
		return phoneInputAdapterRest.historial(database);
	}

	@Operation(summary = "Crear telefono", description = "Crea un telefono asociado a una persona (ownerDni). Si el numero ya existe se reemplaza. La base de la ruta manda sobre el campo database del cuerpo.")
	@ApiResponse(responseCode = "201", description = "Telefono creado")
	@PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public PhoneResponse crearTelefono(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@RequestBody PhoneRequest request) throws InvalidOptionException {
		log.info("Into crearTelefono REST API");
		return phoneInputAdapterRest.crearTelefono(database, request);
	}

	@Operation(summary = "Buscar telefono", description = "Devuelve el telefono con el numero indicado.")
	@ApiResponse(responseCode = "200", description = "Telefono encontrado")
	@GetMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PhoneResponse buscarTelefono(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Numero del telefono", example = "3001112233") @PathVariable String number)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarTelefono REST API");
		return phoneInputAdapterRest.buscarTelefono(database, number);
	}

	@Operation(summary = "Editar telefono", description = "Reemplaza los datos del telefono. El numero de la ruta manda sobre el del cuerpo.")
	@ApiResponse(responseCode = "200", description = "Telefono editado")
	@PutMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PhoneResponse editarTelefono(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Numero del telefono", example = "3001112233") @PathVariable String number,
			@RequestBody PhoneRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarTelefono REST API");
		return phoneInputAdapterRest.editarTelefono(database, number, request);
	}

	@Operation(summary = "Eliminar telefono", description = "Elimina el telefono con el numero indicado.")
	@ApiResponse(responseCode = "200", description = "Telefono eliminado")
	@DeleteMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarTelefono(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Numero del telefono", example = "3001112233") @PathVariable String number)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarTelefono REST API");
		if (Boolean.TRUE.equals(phoneInputAdapterRest.eliminarTelefono(database, number))) {
			return Response.ok("The phone with number " + number + " was deleted");
		}
		throw new IllegalStateException("The phone with number " + number + " could not be deleted");
	}

	@Operation(summary = "Contar telefonos", description = "Devuelve cuantos telefonos hay en la base indicada.")
	@ApiResponse(responseCode = "200", description = "Cantidad de telefonos")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contarTelefonos(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarTelefonos REST API");
		return phoneInputAdapterRest.contarTelefonos(database);
	}
}
