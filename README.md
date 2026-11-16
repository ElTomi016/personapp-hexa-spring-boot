# PersonApp - Servicio web en arquitectura hexagonal

Laboratorio de Arquitectura de Software: servicio web en arquitectura hexagonal (puertos y adaptadores) con los patrones Repository y Service (caso de uso). Expone el CRUD completo de **Persona**, **Profesion**, **Telefono** y **Estudios** sobre dos motores de persistencia (**MariaDB** y **MongoDB**), con dos adaptadores de entrada: **API REST** documentada con Swagger (OpenAPI 3) y **CLI** por consola.

Repositorio base: https://github.com/andres-karoll/personapp-hexa-spring-boot (este repositorio es un fork de trabajo).

## Contenido

1. [Stack](#1-stack)
2. [Arquitectura y modulos](#2-arquitectura-y-modulos)
3. [Modelo de datos](#3-modelo-de-datos)
4. [Configuracion](#4-configuracion)
5. [Despliegue rapido con Docker](#5-despliegue-rapido-con-docker)
6. [Ejecucion local sin contenedores de aplicacion](#6-ejecucion-local-sin-contenedores-de-aplicacion)
7. [API REST](#7-api-rest)
8. [CLI](#8-cli)
9. [Pruebas](#9-pruebas)
10. [Scripts DDL y DML](#10-scripts-ddl-y-dml)
11. [Solucion de problemas](#11-solucion-de-problemas)
12. [Flujo de trabajo en git y evidencias](#12-flujo-de-trabajo-en-git-y-evidencias)

## 1. Stack

| Componente | Detalle |
|---|---|
| Lenguaje | JDK 11 |
| Framework | Spring Boot 2.7.18 (Spring Framework 5.3.31) |
| Persistencia | MariaDB 10.11 (Spring Data JPA, driver 3.5.11) y MongoDB 6 (Spring Data MongoDB) |
| Entrada | REST (Spring Web) y CLI (consola) |
| Documentacion de API | Swagger UI / OpenAPI 3 (springdoc-openapi-ui 1.8.0) |
| Build | Maven multi-modulo (Maven 3.9) |
| Contenedores | Docker y Docker Compose |

Se permanece en la linea Spring Boot 2.7.x porque Spring Boot 3 exige JDK 17 o superior y cambia `javax` por `jakarta`. La tabla de versiones antes y despues del upgrade esta en [`evidencias/versiones-antes-despues.md`](evidencias/versiones-antes-despues.md).

## 2. Arquitectura y modulos

```
personapp-hexa-spring-boot
  common                 anotaciones (@Adapter, @Mapper, @Port, @UseCase), excepciones, DatabaseOption
  domain                 Person, Phone, Profession, Study, Gender
  application            port/in (casos de uso), port/out (persistencia), usecase/
  maria-output-adapter   entity/, mapper/, repository/, adapter/   (JPA)
  mongo-output-adapter   document/, mapper/, repository/, adapter/ (Spring Data MongoDB)
  rest-input-adapter     controller/, adapter/, mapper/, model/    (aplicacion Spring Boot REST)
  cli-input-adapter      terminal/menu/, adapter/, mapper/, model/ (aplicacion Spring Boot CLI)
  scripts                DDL y DML de MariaDB y MongoDB
  evidencias             salidas de consola, tablas y pruebas del proceso
```

- **Dominio y aplicacion** no dependen de ningun framework de persistencia ni de entrada. Los casos de uso (`Service`) solo conocen los puertos.
- **Puertos de salida** (`PersonOutputPort`, `ProfessionOutputPort`, `PhoneOutputPort`, `StudyOutputPort`) los implementan los adaptadores de Maria y Mongo (patron Repository). Cada adaptador se registra con un nombre: `personOutputAdapterMaria`, `professionOutputAdapterMongo`, `phoneOutputAdapterMaria`, `studyOutputAdapterMongo`, etc.
- **Adaptadores de entrada** (REST y CLI) eligen el motor por peticion: el caso de uso se resuelve con la base indicada (`maria` o `mongo`), sin estado compartido entre peticiones.
- **Mappers** traducen entre dominio y entidades/documentos. Para evitar ciclos (Persona - Telefono - Persona, Persona - Estudios - Persona) los objetos anidados se convierten en forma superficial, es decir, sin sus listas.
- REST y CLI son **dos aplicaciones Spring Boot distintas**, cada una con su `application.properties`.

## 3. Modelo de datos

| Tabla (MariaDB) | Columnas | Claves |
|---|---|---|
| `persona` | `cc` INT(15), `nombre` VARCHAR(45), `apellido` VARCHAR(45), `genero` ENUM('M','F'), `edad` INT(3) nulo | PK `cc` |
| `profesion` | `id` INT(6), `nom` VARCHAR(90), `des` TEXT nulo | PK `id` |
| `telefono` | `num` VARCHAR(15), `oper` VARCHAR(45), `duenio` INT(15) | PK `num`; FK `duenio` -> `persona.cc` |
| `estudios` | `id_prof` INT(6), `cc_per` INT(15), `fecha` DATE nulo, `univer` VARCHAR(50) nulo | PK (`id_prof`, `cc_per`); FK a `profesion.id` y `persona.cc` |

Relaciones: una persona tiene muchos telefonos (1 a N) y persona y profesion se relacionan N a M mediante `estudios`.

En MongoDB existen las colecciones `persona`, `profesion`, `telefono` y `estudios`. `telefono.primaryDuenio` guarda el `_id` de la persona, y `estudios` usa el id `"<cc>-<idProfesion>"` con `primaryPersona` y `primaryProfesion` como referencias. Mongo no tiene llaves foraneas ni cascade: los adaptadores borran los telefonos y estudios dependientes al eliminar una persona o una profesion.

Nota: en MariaDB el genero solo admite `M` y `F`; `Gender.OTHER` se rechaza al escribir.

## 4. Configuracion

Valores por defecto (en `application.properties` de `rest-input-adapter` y `cli-input-adapter`):

| Elemento | Valor |
|---|---|
| MariaDB | `localhost:3307`, base `persona_db`, usuario `persona_db`, clave `persona_db` |
| MongoDB | `localhost:27017`, base `persona_db`, usuario `persona_db`, clave `persona_db`, `authentication-database=admin` |
| REST | puerto `3000` |
| Swagger UI | http://localhost:3000/swagger-ui.html |
| OpenAPI JSON | http://localhost:3000/api-docs |
| Logs | `logs/persona.log` (relativo al directorio desde donde se ejecuta) |

Cualquier propiedad se puede sobrescribir con variables de entorno, sin cambiar codigo. El `docker-compose.yml` usa:

```
SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/persona_db
SPRING_DATA_MONGODB_HOST=mongo
```

## 5. Despliegue rapido con Docker

Requisitos: Docker Desktop (o Docker Engine) con Docker Compose v2. No hace falta instalar JDK ni Maven: la imagen compila el proyecto.

```bash
# desde la raiz del repositorio: bases de datos + API REST
docker compose up -d --build
docker compose ps
```

- MariaDB queda en `localhost:3307` y MongoDB en `localhost:27017`, ya con los scripts DDL y DML aplicados.
- La API REST queda en http://localhost:3000 y Swagger en http://localhost:3000/swagger-ui.html.
- Los servicios esperan a que las bases esten sanas (`healthcheck`) antes de arrancar `rest`.

CLI (servicio opcional, en el perfil `cli`):

```bash
docker compose run --build --rm cli
```

Use `--build` en cada ejecucion despues de cambiar codigo: `docker compose up --build` no reconstruye la imagen del CLI porque el servicio esta en un perfil.

Apagar y reiniciar desde cero:

```bash
docker compose down        # detiene; conserva los datos
docker compose down -v     # detiene y borra los volumenes (los scripts vuelven a ejecutarse)
```

Los scripts de `scripts/` se ejecutan solo cuando el volumen esta vacio. Si los modifica, use `docker compose down -v`.

## 6. Ejecucion local sin contenedores de aplicacion

Requisitos: JDK 11, Maven 3.9 y Docker (solo para las bases). En el IDE debe estar instalado el plugin de Lombok.

```bash
# 1. Bases de datos con los scripts aplicados
docker compose up -d mariadb mongo

# 2. Compilar todos los modulos (con JDK 11)
export JAVA_HOME=/ruta/al/jdk-11
mvn clean install -DskipTests

# 3a. API REST
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar

# 3b. CLI (en otra terminal)
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
```

En macOS con Homebrew: `brew install openjdk@11` y `export JAVA_HOME=/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home`.

Sin Docker tambien puede instalar MariaDB (puerto 3307) y MongoDB (puerto 27017) y ejecutar manualmente los scripts de la seccion 10.

## 7. API REST

La ruta lleva siempre el motor: `{database}` es `maria` o `mongo`. La base de la ruta manda sobre el campo `database` del cuerpo. Documentacion interactiva y ejemplos en Swagger UI.

Cada modulo expone las seis operaciones: tres de escritura (crear, editar, eliminar) y tres de lectura (listar, buscar, contar).

| Operacion | Persona | Profesion | Telefono | Estudios |
|---|---|---|---|---|
| Listar | `GET /api/v1/persona/{database}` | `GET /api/v1/profesion/{database}` | `GET /api/v1/telefono/{database}` | `GET /api/v1/estudios/{database}` |
| Crear | `POST /api/v1/persona/{database}` | `POST /api/v1/profesion/{database}` | `POST /api/v1/telefono/{database}` | `POST /api/v1/estudios/{database}` |
| Buscar | `GET .../persona/{database}/{cc}` | `GET .../profesion/{database}/{id}` | `GET .../telefono/{database}/{number}` | `GET .../estudios/{database}/{cc}/{professionId}` |
| Editar | `PUT` en la ruta de buscar | `PUT` en la ruta de buscar | `PUT` en la ruta de buscar | `PUT` en la ruta de buscar |
| Eliminar | `DELETE` en la ruta de buscar | `DELETE` en la ruta de buscar | `DELETE` en la ruta de buscar | `DELETE` en la ruta de buscar |
| Contar | `GET .../persona/{database}/count` | `GET .../profesion/{database}/count` | `GET .../telefono/{database}/count` | `GET .../estudios/{database}/count` |

Agregacion (listas dentro de Persona):

| Ruta | Descripcion |
|---|---|
| `GET /api/v1/persona/{database}/{cc}/telefonos` | Telefonos de la persona |
| `GET /api/v1/persona/{database}/{cc}/estudios` | Estudios de la persona |

Reglas de negocio y errores:

- Crear un telefono exige que la persona (`ownerDni`) exista; crear un estudio exige que existan la persona (`dni`) y la profesion (`professionId`). Si no existen, la respuesta es `404`.
- Crear con un identificador existente reemplaza el registro. La fecha de estudios usa el formato `yyyy-MM-dd`.
- Un registro inexistente responde `404` con un cuerpo `{"status":"NOT_FOUND","description":"...","date":"..."}`.

Ejemplos:

```bash
curl localhost:3000/api/v1/persona/maria
curl localhost:3000/api/v1/persona/mongo/123456789/telefonos
curl localhost:3000/api/v1/estudios/maria/123456789/1
curl localhost:3000/api/v1/profesion/mongo/count
```

## 8. CLI

El menu principal ofrece los cuatro modulos (Personas, Profesiones, Telefonos, Estudios). Despues de elegir un modulo se elige el motor (1 MariaDB, 2 MongoDB) y aparecen las seis operaciones:

```
1 para crear      2 para editar     3 para eliminar
4 para ver todo   5 para buscar     6 para contar
0 para regresar
```

El modulo de Personas agrega `7` (ver los telefonos de una persona) y `8` (ver los estudios de una persona). Eliminar pide confirmacion.

## 9. Pruebas

```bash
# pruebas unitarias de mappers (no necesitan bases)
mvn clean install

# pruebas de integracion de los adaptadores (necesitan docker compose up -d mariadb mongo)
mvn clean install -Dit=true
```

Las pruebas de integracion usan los datos de `scripts/persona_dml_*` y limpian lo que crean. Sin `-Dit=true` se omiten.

## 10. Scripts DDL y DML

| Archivo | Contenido |
|---|---|
| `scripts/persona_ddl_maria.sql` | Elimina y recrea el usuario y el esquema `persona_db`, y crea las cuatro tablas |
| `scripts/persona_dml_maria.sql` | 5 personas, 4 profesiones, 5 telefonos y 4 estudios |
| `scripts/persona_ddl_mongo.js` | Crea el usuario `persona_db` en `admin` y las cuatro colecciones |
| `scripts/persona_dml_mongo.js` | Los mismos datos de ejemplo que en MariaDB |

Advertencia: `persona_ddl_maria.sql` ejecuta `DROP USER` y `DROP SCHEMA`; usarlo solo en ambientes de desarrollo.

Ejecucion manual (sin compose):

```bash
mariadb -h 127.0.0.1 -P 3307 -uroot -p < scripts/persona_ddl_maria.sql
mariadb -h 127.0.0.1 -P 3307 -uroot -p < scripts/persona_dml_maria.sql
mongosh --port 27017 scripts/persona_ddl_mongo.js
mongosh --port 27017 scripts/persona_dml_mongo.js
```

## 11. Solucion de problemas

| Sintoma | Causa y solucion |
|---|---|
| `Cannot connect to the Docker daemon` | Docker Desktop esta apagado. Abrirlo y repetir. |
| Error de compilacion de Lombok o `release 11` | El JDK activo no es el 11. Revisar `java -version` y `JAVA_HOME`. |
| `java -jar` dice que no hay atributo de manifiesto principal | Falto compilar con `mvn clean install`; los poms de REST y CLI ejecutan `repackage`. |
| La imagen del CLI no refleja los cambios | Usar `docker compose run --build --rm cli`. |
| Los datos de ejemplo no aparecen o estan viejos | Los scripts solo corren con el volumen vacio: `docker compose down -v` y volver a levantar. |
| Puerto ocupado (3000, 3307 o 27017) | Detener el proceso que lo usa o cambiar el puerto publicado en `docker-compose.yml`. |
| Las pruebas de integracion fallan con `Failed to load ApplicationContext` | Las bases no estan arriba: `docker compose up -d mariadb mongo`. |

## 12. Flujo de trabajo en git y evidencias

- `main` contiene la version estable; `develop` integra el trabajo del equipo; cada tarea va en una rama `feature/<tarea>` y entra por pull request revisado por otro integrante.
- Tag `baseline`: estado del proyecto con contenedores y antes de corregir los mappers.
- La carpeta [`evidencias/`](evidencias) guarda el error original de los mappers (`StackOverflowError`), la comprobacion de que quedo corregido, la salida de `docker compose ps` y la tabla de versiones.
- Licencia: ver [`LICENSE`](LICENSE).
