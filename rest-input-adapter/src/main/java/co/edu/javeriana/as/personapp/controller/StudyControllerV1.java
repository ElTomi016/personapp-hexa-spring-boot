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

import co.edu.javeriana.as.personapp.adapter.StudyInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
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
@RequestMapping("/api/v1/estudios")
@Tag(name = "Estudios", description = "Operaciones sobre estudios (persona + profesion) en MariaDB o MongoDB")
public class StudyControllerV1 {

	@Autowired
	private StudyInputAdapterRest studyInputAdapterRest;

	@Operation(summary = "Listar estudios", description = "Devuelve todos los estudios de la base indicada.")
	@ApiResponse(responseCode = "200", description = "Lista de estudios")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<StudyResponse> estudios(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into estudios REST API");
		return studyInputAdapterRest.historial(database);
	}

	@Operation(summary = "Crear estudio", description = "Crea el estudio de una persona (dni) en una profesion (professionId); ambas deben existir (404 si no). La fecha usa el formato yyyy-MM-dd. Si la pareja ya existe se reemplaza. La base de la ruta manda sobre el campo database del cuerpo.")
	@ApiResponse(responseCode = "201", description = "Estudio creado")
	@PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public StudyResponse crearEstudio(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@RequestBody StudyRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into crearEstudio REST API");
		return studyInputAdapterRest.crearEstudio(database, request);
	}

	@Operation(summary = "Buscar estudio", description = "Devuelve el estudio de la persona en la profesion indicada (clave compuesta).")
	@ApiResponse(responseCode = "200", description = "Estudio encontrado")
	@GetMapping(path = "/{database}/{cc}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public StudyResponse buscarEstudio(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc,
			@Parameter(description = "Id de la profesion", example = "1") @PathVariable Integer professionId)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarEstudio REST API");
		return studyInputAdapterRest.buscarEstudio(database, cc, professionId);
	}

	@Operation(summary = "Editar estudio", description = "Reemplaza fecha y universidad del estudio. Los ids de la ruta mandan sobre los del cuerpo.")
	@ApiResponse(responseCode = "200", description = "Estudio editado")
	@PutMapping(path = "/{database}/{cc}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public StudyResponse editarEstudio(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc,
			@Parameter(description = "Id de la profesion", example = "1") @PathVariable Integer professionId,
			@RequestBody StudyRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarEstudio REST API");
		return studyInputAdapterRest.editarEstudio(database, cc, professionId, request);
	}

	@Operation(summary = "Eliminar estudio", description = "Elimina el estudio de la persona en la profesion indicada.")
	@ApiResponse(responseCode = "200", description = "Estudio eliminado")
	@DeleteMapping(path = "/{database}/{cc}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarEstudio(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database,
			@Parameter(description = "Dni (cc) de la persona", example = "123456789") @PathVariable Integer cc,
			@Parameter(description = "Id de la profesion", example = "1") @PathVariable Integer professionId)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarEstudio REST API");
		if (Boolean.TRUE.equals(studyInputAdapterRest.eliminarEstudio(database, cc, professionId))) {
			return Response.ok("The study of person " + cc + " and profession " + professionId + " was deleted");
		}
		throw new IllegalStateException(
				"The study of person " + cc + " and profession " + professionId + " could not be deleted");
	}

	@Operation(summary = "Contar estudios", description = "Devuelve cuantos estudios hay en la base indicada.")
	@ApiResponse(responseCode = "200", description = "Cantidad de estudios")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contarEstudios(
			@Parameter(description = ApiDocs.DATABASE_DESCRIPTION, example = ApiDocs.DATABASE_EXAMPLE, schema = @Schema(allowableValues = { "maria", "mongo" })) @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarEstudios REST API");
		return studyInputAdapterRest.contarEstudios(database);
	}
}
