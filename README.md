# Wishlist API - Carvajal

## Descripción
API REST para gestionar autenticación, usuarios y listas de deseos en el sistema de Carvajal.

## Requisitos
- Java 21
- Maven
- PostgreSQL
- Docker

## Configuración

### Base de Datos
1. Crear una base de datos PostgreSQL llamada `wishlist_db`.
2. Configurar las credenciales en `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/wishlist_db
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

### JWT
Configurar el secreto para JWT en `application.properties`:
```properties
jwt.secret=mi_secreto_seguro_1234567890_minimo_32_bytes_para_hmac
jwt.expiration=86400000
```

## Ejecución Local
1. Clonar el repositorio.
2. Configurar la base de datos y JWT.
3. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

## Docker
1. Construir y ejecutar los servicios con Docker Compose:
   ```bash
   docker-compose up --build
   ```

## Endpoints

### Autenticación
- `POST /api/auth/register`: Registrar usuario (público).
- `POST /api/auth/login`: Iniciar sesión (público).

### Lista de Deseos
- `GET /api/wishlist`: Listar productos en la lista de deseos (solo CLIENT).
- `POST /api/wishlist`: Agregar producto a la lista (solo CLIENT).
- `DELETE /api/wishlist/{productId}`: Eliminar producto de la lista (solo CLIENT).
- `GET /api/wishlist/history`: Listar histórico de la lista (solo CLIENT).

### Administración
- `PUT /api/admin/users/{userId}/role`: Actualizar rol de usuario (solo ADMIN).

## Documentación
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## GitFlow
- Rama principal: `main`
- Rama de desarrollo: `develop-dev`
- Ramas de funcionalidades: `feature/*`
- Ramas personales: `NombreApellidoDev`

## Contribuidores
- Janner Escobar
- Michael Vera

**Notas:**
- Incluir instrucciones claras para configurar y ejecutar el proyecto.
- Documentar los endpoints principales.
- Incluir información sobre GitFlow y cómo contribuir.
