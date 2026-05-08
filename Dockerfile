# Etapa 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw

# Descargar dependencias (cache)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Crear usuario no root por seguridad
RUN useradd --create-home --shell /bin/bash appuser
USER appuser

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Variables de entorno recomendadas
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["java", "-jar", "app.jar"]