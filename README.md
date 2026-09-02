# Carvajal Wishlist Backend

API REST desarrollada como parte de la prueba técnica de Carvajal para gestionar un catálogo de productos y una lista de deseos.

## Descripción

La aplicación permite:

- Consultar el catálogo de productos activos.
- Consultar un producto por ID.
- Crear, actualizar y eliminar productos.
- Consultar, crear, actualizar y eliminar elementos de la lista de deseos.
- Validar disponibilidad de stock.
- Persistir la información en PostgreSQL.
- Documentar la API mediante OpenAPI/Swagger.
- Ejecutar la aplicación de forma local o mediante Docker Compose.

## Arquitectura Cliente–Servidor

La solución utiliza una arquitectura **Cliente–Servidor**.

El cliente será desarrollado con **Angular** y se comunicará mediante HTTP con el servidor. El servidor será desarrollado con **Spring Boot** y expondrá una API REST encargada de procesar las solicitudes, ejecutar la lógica de negocio, validar la información y acceder a PostgreSQL mediante JPA/Hibernate.

### Arquitectura general

```text
┌──────────────────────────────┐
│           CLIENTE            │
│           Angular            │
│                              │
│  • Catálogo de productos     │
│  • Lista de deseos           │
│  • Interfaz de usuario       │
└──────────────┬───────────────┘
               │
               │ HTTP / REST
               ▼
┌──────────────────────────────┐
│           SERVIDOR           │
│          Spring Boot         │
│                              │
│  Controllers                 │
│       ↓                      │
│  Services                    │
│       ↓                      │
│  Repositories                │
└──────────────┬───────────────┘
               │
               │ JPA / Hibernate
               ▼
┌──────────────────────────────┐
│          PostgreSQL          │
│        Base de datos         │
└──────────────────────────────┘
Responsabilidades
Cliente — Angular

Se encargará de la presentación y de la interacción con el usuario, consumiendo los endpoints REST expuestos por el servidor.

Entre sus principales funcionalidades se encuentran:

Visualización del catálogo.
Consulta de cantidades disponibles.
Gestión de la lista de deseos.
Interacción con la API REST.
Servidor — Spring Boot

Se encargará de:

Exponer la API REST.
Procesar las solicitudes del cliente.
Aplicar las reglas de negocio.
Validar los datos recibidos.
Gestionar la seguridad y autorización.
Consultar y modificar la información persistida.
Base de datos — PostgreSQL

Se encargará de almacenar la información de productos y elementos de la lista de deseos.

Este repositorio contiene actualmente el componente servidor de la solución. La arquitectura completa contempla Angular como cliente y Spring Boot como servidor.

Tecnologías
Java 21
Spring Boot 4.1.0
Spring Web MVC
Spring Data JPA
Hibernate
Spring Security
PostgreSQL 16
Springdoc OpenAPI
Swagger UI
Maven
Docker
Docker Compose
JUnit
Spring Boot Test
Estructura del proyecto

El servidor Spring Boot utiliza una arquitectura por capas:

src/main/java/com/carvajal/wishlist
├── config
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller
│   ├── ProductController.java
│   └── WishlistItemController.java
├── dto
│   ├── ProductDTO.java
│   └── WishlistItemDTO.java
├── entity
│   ├── Product.java
│   └── WishlistItem.java
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── StockNotAvailableException.java
├── repository
│   ├── ProductRepository.java
│   └── WishlistItemRepository.java
└── service
    ├── ProductService.java
    └── WishlistItemService.java
Requisitos

Para ejecutar el proyecto localmente se requiere:

Java 21
Maven 3.9+ o Maven Wrapper
PostgreSQL 16 o compatible

Para ejecutar mediante Docker:

Docker
Docker Compose
Configuración de base de datos

La aplicación utiliza PostgreSQL.

Base de datos:

wishlist_db

Usuario:

postgres

La conexión se configura mediante variables de entorno:

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/wishlist_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
Variables de entorno

Crear un archivo .env en la raíz del proyecto:

DB_PASSWORD=TU_CONTRASEÑA_DE_POSTGRES

El archivo .env contiene información sensible y no debe subirse al repositorio.

Ejecución local

Desde la raíz del proyecto:

./mvnw clean test

Para iniciar la aplicación:

./mvnw spring-boot:run

La aplicación se ejecutará por defecto en:

http://localhost:8080
Ejecución con Docker

El proyecto incluye:

Dockerfile
docker-compose.yml
.dockerignore
Construir la imagen
docker build -t carvajal-wishlist-backend:latest .
Levantar los servicios
docker compose up -d
Verificar los contenedores
docker compose ps

La configuración actual utiliza:

Servicio	Puerto host	Puerto contenedor
Spring Boot	8081	8080
PostgreSQL	5433	5432

Por lo tanto:

API:
http://localhost:8081

PostgreSQL:
localhost:5433
Detener los servicios
docker compose down
Ver logs del backend
docker compose logs app --tail=100
Ver logs de PostgreSQL
docker compose logs postgres --tail=50
Swagger / OpenAPI

La API cuenta con documentación mediante Swagger UI.

Con Docker
http://localhost:8081/swagger-ui/index.html
Ejecución local
http://localhost:8080/swagger-ui/index.html

El documento OpenAPI también está disponible mediante:

http://localhost:8081/v3/api-docs
Seguridad

La aplicación utiliza Spring Security con HTTP Basic.

Para el entorno de prueba se dispone de un usuario administrativo:

Usuario: admin
Contraseña: admin123
Rol: ADMIN

Estas credenciales son únicamente para demostración. En un ambiente productivo deben utilizarse credenciales seguras y un mecanismo adecuado de gestión de usuarios.

Los endpoints de administración de productos requieren el rol ADMIN.

API REST
Productos

Base URL:

/api/products
Método	Endpoint	Descripción	Autorización
GET	/api/products	Listar productos	Pública
GET	/api/products/{id}	Consultar producto	Pública
POST	/api/products	Crear producto	ADMIN
PUT	/api/products/{id}	Actualizar producto	ADMIN
DELETE	/api/products/{id}	Eliminar producto	ADMIN
Ejemplo de producto
{
  "name": "Producto de ejemplo",
  "description": "Descripción del producto",
  "price": 100.00,
  "stock": 10,
  "isActive": true
}
Lista de deseos

Base URL:

/api/wishlist

La lista de deseos permite gestionar los productos que el cliente desea comprar posteriormente.

Método	Endpoint	Descripción
GET	/api/wishlist	Listar elementos de la wishlist
GET	/api/wishlist/{id}	Consultar elemento por ID
POST	/api/wishlist	Agregar elemento
PUT	/api/wishlist/{id}	Actualizar elemento
DELETE	/api/wishlist/{id}	Eliminar elemento
Ejemplo
{
  "name": "Producto deseado",
  "url": "https://example.com/producto",
  "price": 150.00,
  "purchased": false
}
Modelo de datos
Producto

La entidad Product contiene información del catálogo, incluyendo:

ID
Nombre
Descripción
Precio
Stock
Estado activo
Fecha de creación
Fecha de actualización
Wishlist Item

La entidad WishlistItem contiene información de los elementos registrados en la lista de deseos:

ID
Nombre
URL
Precio
Estado de compra
Fecha de creación
Manejo de errores

La aplicación cuenta con un manejador global de excepciones mediante:

GlobalExceptionHandler
Recurso no encontrado

Cuando un producto o elemento no existe, la API devuelve:

404 Not Found
Stock no disponible

Cuando no existe suficiente stock para una operación, se utiliza:

409 Conflict

mediante la excepción:

StockNotAvailableException
Validaciones

Los DTO utilizan Bean Validation.

Productos
El nombre es obligatorio.
El nombre tiene una longitud máxima de 255 caracteres.
El precio debe ser mayor a cero.
El stock no puede ser negativo.
Wishlist
El nombre es obligatorio.
El nombre tiene una longitud máxima de 255 caracteres.
La URL tiene una longitud máxima definida.
El precio no puede ser negativo.
Pruebas

El proyecto incluye pruebas unitarias y de integración para diferentes componentes de la aplicación.

Entre ellas:

ProductController
WishlistItemController
ProductService
WishlistItemService
Contexto de Spring Boot

Para ejecutar todas las pruebas:

./mvnw test

También es posible utilizar:

mvn test
Datos iniciales

El proyecto utiliza:

src/main/resources/data.sql

para la inicialización de la información de la base de datos.

La configuración:

spring.sql.init.mode=always

permite ejecutar la inicialización SQL durante el arranque.

Hibernate también administra la actualización del esquema mediante:

spring.jpa.hibernate.ddl-auto=update
Git

El proyecto utiliza ramas para organizar el desarrollo de funcionalidades.

La rama actual de desarrollo es:

JannerEscobarDev

Ejemplo de creación de una rama:

git checkout -b feature/nueva-funcionalidad

Agregar cambios:

git add .

Crear commit:

git commit -m "feat: nueva funcionalidad"

Subir la rama:

git push origin feature/nueva-funcionalidad

Posteriormente, los cambios pueden integrarse mediante Pull Request hacia la rama de desarrollo correspondiente.

Despliegue rápido con Docker

Clonar el repositorio:

git clone <URL_DEL_REPOSITORIO>

Ingresar al proyecto:

cd carvajal-wishlist-backend

Crear el archivo .env:

DB_PASSWORD=TU_CONTRASEÑA_DE_POSTGRES

Construir la imagen:

docker build -t carvajal-wishlist-backend:latest .

Levantar los servicios:

docker compose up -d

Verificar:

docker compose ps

Acceder a Swagger:

http://localhost:8081/swagger-ui/index.html
Mejoras futuras

Como posibles mejoras para una siguiente iteración:

Implementar autenticación mediante JWT.
Persistir usuarios y roles en base de datos.
Asociar cada wishlist con un usuario autenticado.
Relacionar cada elemento de wishlist directamente con un producto del catálogo.
Implementar una consulta específica para detectar productos sin stock en la wishlist.
Incorporar migraciones mediante Flyway o Liquibase.
Aumentar las pruebas de integración utilizando una base de datos aislada.
Completar el cliente Angular para consumir la API REST.
Incorporar CI/CD para automatizar pruebas y despliegues.
Alcance

Este repositorio contiene el servidor de la solución, desarrollado con Spring Boot y expuesto mediante una API REST.

La arquitectura completa de la solución está planteada bajo el modelo Cliente–Servidor, donde:

Angular
   ↓
HTTP / REST
   ↓
Spring Boot
   ↓
JPA / Hibernate
   ↓
PostgreSQL

El cliente Angular constituye la capa de presentación y consume los servicios proporcionados por el backend.

Prueba técnica

El proyecto fue desarrollado tomando como referencia los requerimientos de la prueba técnica de Carvajal, que contempla una solución E-commerce con catálogo de productos y lista de deseos.

Los principales requerimientos considerados son:

Catálogo de productos.
Consulta de cantidades disponibles.
Gestión de lista de deseos.
Notificación de productos sin stock.
Persistencia del histórico de la wishlist.
API REST.
Persistencia mediante ORM.
Pruebas unitarias y de integración.
Documentación del proyecto.
Contenerización mediante Docker.