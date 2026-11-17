# [Integrante A] Build multi-stage. ARG MODULE elige que app empaquetar (rest-input-adapter o cli-input-adapter).
# Etapa 1: compila todo con Maven + JDK 11 (sin tests).
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /src
COPY . .
RUN mvn -B -q -DskipTests package

# Etapa 2: imagen liviana solo con el JRE y el jar ejecutable (requiere "repackage" en el pom).
FROM eclipse-temurin:11-jre
ARG MODULE
WORKDIR /app
COPY --from=build /src/${MODULE}/target/${MODULE}-*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
