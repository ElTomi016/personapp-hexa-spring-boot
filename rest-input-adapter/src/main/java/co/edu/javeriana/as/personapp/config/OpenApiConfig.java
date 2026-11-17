package co.edu.javeriana.as.personapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/** [Integrante B] Configuracion de Swagger/OpenAPI (springdoc): http://localhost:3000/swagger-ui.html */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI personAppOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("PersonApp API")
				.version("v1")
				.description("Servicio REST hexagonal para Persona, Profesion, Telefono y Estudios. "
						+ "Cada operacion recibe la base de datos en la ruta: maria (MariaDB) o mongo (MongoDB)."));
	}
}
