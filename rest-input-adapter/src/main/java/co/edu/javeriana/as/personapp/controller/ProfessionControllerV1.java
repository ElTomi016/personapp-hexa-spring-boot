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

import co.edu.javeriana.as.personapp.adapter.ProfessionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfessionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfessionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/profesion")
@Tag(name = "Profesion", description = "Operaciones sobre profesiones en MariaDB o MongoDB")
public class ProfessionControllerV1 {

	@Autowired
	private ProfessionInputAdapterRest professionInputAdapterRest;

	@Operation(summary = "Listar profesiones", description = "Devuelve todas las profesiones de la base indicada.")
	@ApiResponse(responseCode = "200", description = "Lista de profesiones")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProfessionResponse> profesiones(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into profesiones REST API");
		return professionInputAdapterRest.historial(database);
	}

	@Operation(summary = "Crear profesion", description = "Crea una profesion. Si el id ya existe se reemplaza. La base de la ruta manda sobre el campo database del cuerpo.")
	@ApiResponse(responseCode = "201", description = "Profesion creada")
	@PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ProfessionResponse crearProfesion(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@RequestBody ProfessionRequest request) throws InvalidOptionException {
		log.info("Into crearProfesion REST API");
		return professionInputAdapterRest.crearProfesion(database, request);
	}

	@Operation(summary = "Buscar profesion", description = "Devuelve la profesion con el id indicado.")
	@ApiResponse(responseCode = "200", description = "Profesion encontrada")
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProfessionResponse buscarProfesion(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Id de la profesion", example = "1") @PathVariable Integer id)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarProfesion REST API");
		return professionInputAdapterRest.buscarProfesion(database, id);
	}

	@Operation(summary = "Editar profesion", description = "Reemplaza los datos de la profesion. El id de la ruta manda sobre el del cuerpo.")
	@ApiResponse(responseCode = "200", description = "Profesion editada")
	@PutMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfessionResponse editarProfesion(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Id de la profesion", example = "1") @PathVariable Integer id,
			@RequestBody ProfessionRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarProfesion REST API");
		return professionInputAdapterRest.editarProfesion(database, id, request);
	}

	@Operation(summary = "Eliminar profesion", description = "Elimina la profesion con el id indicado.")
	@ApiResponse(responseCode = "200", description = "Profesion eliminada")
	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarProfesion(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Id de la profesion", example = "1") @PathVariable Integer id)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarProfesion REST API");
		if (Boolean.TRUE.equals(professionInputAdapterRest.eliminarProfesion(database, id))) {
			return Response.ok("The profession with id " + id + " was deleted");
		}
		throw new IllegalStateException("The profession with id " + id + " could not be deleted");
	}

	@Operation(summary = "Contar profesiones", description = "Devuelve cuantas profesiones hay en la base indicada.")
	@ApiResponse(responseCode = "200", description = "Cantidad de profesiones")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contarProfesiones(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarProfesiones REST API");
		return professionInputAdapterRest.contarProfesiones(database);
	}
}
