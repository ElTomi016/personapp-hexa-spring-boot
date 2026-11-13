# Versiones antes y despues del upgrade

| Componente | Antes | Despues |
|---|---|---|
| Spring Boot | 2.7.11 | 2.7.18 |
| Spring Framework | 5.3.27 | 5.3.31 |
| springdoc-openapi-ui | 1.7.0 | 1.8.0 |
| Lombok | 1.18.26 | 1.18.48 |
| Driver MariaDB | 3.1.4 | 3.5.11 |
| log4j-to-slf4j / log4j-api | 2.20.0 | 2.26.1 |
| JAXB (solo CLI) | api 2.3.1, core/impl 2.3.0.1 | sin cambio (se mantiene javax) |
| JDK | 11 | 11 |

Spring Boot 3.x y Spring 6/7 exigen JDK 17 o superior y migran javax a jakarta, por eso se permanece en la linea 2.7.x. mariadb-java-client 4.x y JAXB 4.x se descartan por la misma razon.

Otros cambios de build: BOM spring-boot-dependencies 2.7.18 en dependencyManagement y flatten-maven-plugin para resolver ${revision} en los poms instalados.
