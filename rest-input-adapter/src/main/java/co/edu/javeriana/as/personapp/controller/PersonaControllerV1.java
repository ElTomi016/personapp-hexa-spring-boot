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

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
@Tag(name = "Persona", description = "Operaciones sobre personas en MariaDB o MongoDB")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@Operation(summary = "Listar personas", description = "Devuelve todas las personas de la base indicada.")
	@ApiResponse(responseCode = "200", description = "Lista de personas")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database);
	}
	
	@Operation(summary = "Crear persona", description = "Crea una persona. Si el dni ya existe se reemplaza. La base de la ruta manda sobre el campo database del cuerpo. En MariaDB sex solo admite MALE/FEMALE (o M/F).")
	@ApiResponse(responseCode = "201", description = "Persona creada")
	@PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public PersonaResponse crearPersona(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@RequestBody PersonaRequest request) throws InvalidOptionException {
		log.info("Into crearPersona REST API");
		return personaInputAdapterRest.crearPersona(database, request);
	}

	@Operation(summary = "Buscar persona", description = "Devuelve la persona con el dni indicado.")
	@ApiResponse(responseCode = "200", description = "Persona encontrada")
	@GetMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse buscarPersona(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarPersona REST API");
		return personaInputAdapterRest.buscarPersona(database, cc);
	}

	@Operation(summary = "Editar persona", description = "Reemplaza los datos de la persona. El dni de la ruta manda sobre el del cuerpo; lo que no se envia (por ejemplo age) queda vacio.")
	@ApiResponse(responseCode = "200", description = "Persona editada")
	@PutMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse editarPersona(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc,
			@RequestBody PersonaRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarPersona REST API");
		return personaInputAdapterRest.editarPersona(database, cc, request);
	}

	@Operation(summary = "Eliminar persona", description = "Elimina la persona con el dni indicado.")
	@ApiResponse(responseCode = "200", description = "Persona eliminada")
	@DeleteMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarPersona(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarPersona REST API");
		if (Boolean.TRUE.equals(personaInputAdapterRest.eliminarPersona(database, cc))) {
			return Response.ok("The person with id " + cc + " was deleted");
		}
		throw new IllegalStateException("The person with id " + cc + " could not be deleted");
	}

	@Operation(summary = "Contar personas", description = "Devuelve cuantas personas hay en la base indicada.")
	@ApiResponse(responseCode = "200", description = "Cantidad de personas")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contarPersonas(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarPersonas REST API");
		return personaInputAdapterRest.contarPersonas(database);
	}

	@Operation(summary = "Telefonos de una persona", description = "Devuelve los telefonos de la persona (agregacion).")
	@ApiResponse(responseCode = "200", description = "Lista de telefonos de la persona")
	@GetMapping(path = "/{database}/{cc}/telefonos", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PhoneResponse> telefonosDePersona(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into telefonosDePersona REST API");
		return personaInputAdapterRest.telefonosDePersona(database, cc);
	}

	@Operation(summary = "Estudios de una persona", description = "Devuelve los estudios de la persona (agregacion).")
	@ApiResponse(responseCode = "200", description = "Lista de estudios de la persona")
	@GetMapping(path = "/{database}/{cc}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<StudyResponse> estudiosDePersona(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into estudiosDePersona REST API");
		return personaInputAdapterRest.estudiosDePersona(database, cc);
	}
}
