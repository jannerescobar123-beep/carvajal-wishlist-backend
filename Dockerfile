# Usar imagen base de Java 21
FROM eclipse-temurin:21-jdk-jammy

# Directorio de trabajo
WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw package -DskipTests

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/target/wishlist-0.0.1-SNAPSHOT.jar"]
