<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.4.1-green.svg" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue.svg" alt="PostgreSQL 16" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED.svg" alt="Docker Ready" />
  <img src="https://img.shields.io/badge/JWT-Security-red.svg" alt="JWT Security" />
</div>

<h1 align="center">Sistema de Lista de Deseos (Wishlist API) - Carvajal</h1>

<p align="center">
  Backend RESTful para gestionar la autenticacion de usuarios, perfiles y el sistema central de Listas de Deseos para el ecosistema de comercio electronico de Carvajal. Desarrollada con <b>Spring Boot</b>.
</p>

---

## Indice
- [Descripcion del Proyecto](#descripcion-del-proyecto)
- [Caracteristicas Principales](#caracteristicas-principales)
- [Arquitectura y Tecnologias](#arquitectura-y-tecnologias)
- [Prerequisites](#prerequisites)
- [Estructura de Endpoints](#estructura-de-endpoints)
- [Reglas de Negocio](#reglas-de-negocio)
- [Variables de Entorno](#variables-de-entorno)
- [Despliegue en Render](#despliegue-en-render)
- [Documentacion de la API (Swagger)](#documentacion-de-la-api-swagger)
- [Contribuidores](#contribuidores)

---

## Descripcion del Proyecto

El **Sistema de Lista de Deseos** de Carvajal es un microservicio backend disenado para potenciar la retencion de clientes y facilitar las compras planificadas.

Permite a los usuarios registrarse en la plataforma, explorar un catalogo de productos e ir guardando sus articulos favoritos en una lista de deseos personalizable. La API almacena la lista del usuario, valida la disponibilidad de stock en tiempo real al momento de agregar un producto y mantiene un registro historico inmutable de sus interacciones (agregar/remover) para futuros analisis de inteligencia de negocios o marketing.

---

## Caracteristicas Principales
- **Autenticacion Segura (Stateless):** Implementacion completa de JSON Web Tokens (JWT) con soporte para roles (`ADMIN`, `CLIENT`).
- **Gestion de Usuarios:** Perfilado de usuarios con contrasenas cifradas via `BCrypt` de Spring Security.
- **Lista de Deseos Dinamica:** Endpoints transaccionales para agregar, listar y remover productos de la lista de deseos.
- **Validacion de Inventario en Tiempo Real:** Al agregar un producto, la API valida que este activo y con stock suficiente. El endpoint de consulta de la lista incluye el campo `inStock` con el estado actual de cada producto.
- **Trazabilidad (Historico):** Registro secuencial e historico de interacciones del usuario con su lista.
- **Proteccion CORS Configurada:** Lista para integrarse de inmediato de forma segura con clientes Frontend.
- **Manejo Global de Errores:** Excepciones interceptadas (`@RestControllerAdvice`) y presentadas en un formato JSON estandar y predecible.

---

## Arquitectura y Tecnologias
- **Lenguaje Core:** Java 21
- **Framework Principal:** Spring Boot 3.4.1 (MVC, Data JPA, Security)
- **Capa de Persistencia:** PostgreSQL 16 (Entidades relacionales robustas)
- **Seguridad y Sesiones:** Spring Security + `io.jsonwebtoken`
- **Documentacion de API:** Springdoc OpenAPI (Generacion automatica de Swagger UI)
- **Contenedores y Orquestacion:** Docker y Docker Compose (Entorno encapsulado)

> Este repositorio contiene exclusivamente el **backend REST**. No incluye frontend ni aplicaciones moviles.

---

## Prerequisites

- **Java 21** (JDK para desarrollo, JRE para ejecucion)
- **Maven 3.9+** o usar el Maven Wrapper incluido (`./mvnw`)
- **Docker y Docker Compose** (opcional, para despliegue contenedorizado)
- **PostgreSQL 16** (si se ejecuta sin Docker)

---

## Estructura de Endpoints

### Autenticacion (Publicos)
- `POST /api/auth/register` - Registra un nuevo usuario con rol por defecto `CLIENT`.
- `POST /api/auth/login` - Valida credenciales contra la base de datos y retorna el JWT Token firmado.

### Lista de Deseos (Requiere Token `CLIENT`)
- `GET /api/wishlist` - Obtiene los productos activos en la lista del usuario actual, incluyendo el campo `inStock` con el estado de stock en tiempo real.
- `POST /api/wishlist` - Agrega un producto a la lista de deseos (Requiere `{ productId, quantity }`). Valida stock y estado activo.
- `DELETE /api/wishlist/{productId}` - Elimina un producto especifico de la lista.
- `PUT /api/wishlist/{productId}` - Actualiza la cantidad de un producto en la lista.
- `GET /api/wishlist/history` - Lista el historico de interacciones, ordenado de mas reciente a mas antiguo.

### Administracion (Requiere Token `ADMIN`)
- `PUT /api/admin/users/{userId}/role` - Escala o degrada los permisos de un usuario existente.

### Productos (Publicos para lectura, ADMIN para escritura)
- `GET /api/products` - Lista de productos activos del catalogo.
- `GET /api/products/{id}` - Obtiene un producto por su ID.
- `GET /api/products/{id}/stock` - Verifica si hay stock disponible para un producto.
- `POST /api/products` - Crea un nuevo producto (ADMIN).
- `PUT /api/products/{id}` - Actualiza un producto existente (ADMIN).
- `DELETE /api/products/{id}` - Elimina un producto (ADMIN).

---


## 🎨 Guía Rápida para el Equipo Frontend

¡Hola equipo de Frontend! 👋 Esta sección está diseñada específicamente para que puedan integrar la API de manera rápida y sin fricciones.

### 1. Entornos y Documentación Interactiva
- **Base URL Local:** `http://localhost:8080/api`
- **Swagger UI (Pruebas e interactividad):** `/swagger-ui/index.html`
- **Esquema OpenAPI (Para autogenerar interfaces/tipos en TypeScript):** `/v3/api-docs`

### 2. Flujo de Autenticación (Tokens JWT)
La API utiliza JSON Web Tokens (JWT) de manera **Stateless**. El servidor no guarda sesiones (no existen cookies automáticas).
1. **Login:** Ejecuta un `POST /api/auth/login` enviando las credenciales.
2. **Almacenamiento:** Recibirás un JSON con la propiedad `token`. Cópialo y guárdalo en memoria, `localStorage` o `sessionStorage`.
3. **Peticiones Protegidas:** Para consultar rutas como `/api/wishlist`, inyecta el token en las cabeceras (*Headers*) HTTP usando el esquema `Bearer`:
   ```http
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
   ```

### 3. Estructura Canónica de Errores
Cualquier error HTTP lanzado por la API (400, 401, 403, 404, 409, 500) devolverá **siempre** el mismo formato JSON. Configura tu *Interceptor* (en Axios, Fetch o Angular HttpClient) para leer siempre el campo `message` y mostrárselo al usuario:

```json
{
  "timestamp": "2026-09-11T15:05:55.441",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

### 4. Flujo Recomendado para la Lista de Deseos
1. **Catálogo:** Ejecuta `GET /api/products` (Público). Muestra la lista de productos al usuario.
2. **Intercepción 401:** Si el usuario no está logueado y hace clic en "Añadir a lista", el backend lanzará un `401`. Captúralo y redirige a la vista de Login.
3. **Añadir:** `POST /api/wishlist` (Requiere Token) enviando `{ "productId": 1, "quantity": 1 }`.
4. **Validación Visual de Stock:** Al consultar la lista con `GET /api/wishlist`, cada objeto devolverá la propiedad booleana `inStock`. Si un usuario guardó algo que luego se agotó, esta propiedad vendrá en `false`. **Usa este valor para deshabilitar el botón de "Comprar/Añadir a carrito" en tu UI**.

### 5. Solución a Problemas de CORS
La política de Cross-Origin está estrictamente configurada. Por defecto se permite el tráfico desde Angular (`http://localhost:4200`). Si tu equipo de Frontend levanta React en el `3000` o Vite en el `5173`, el desarrollador backend o devops deberá inyectar la variable de entorno:
`export CORS_ALLOWED_ORIGIN=http://localhost:5173`
De lo contrario, verás un error rojo de CORS en la consola de Chrome/Firefox.

--- 

## Reglas de Negocio
El sistema aplica validaciones criticas mediante Excepciones personalizadas para proteger la integridad de los datos:
1. **Unicidad de Usuario:** No pueden existir dos cuentas con el mismo correo o nombre de usuario (`EmailAlreadyExistsException`, `UsernameAlreadyExistsException`).
2. **Duplicidad en Lista:** Un usuario no puede agregar el mismo producto mas de una vez a su lista activa (`ProductAlreadyInWishlistException`).
3. **Disponibilidad (Stock):** Si el producto se encuentra inactivo o su stock es insuficiente (`quantity` > stock actual), la API denegara la adicion a la lista de deseos (`StockNotAvailableException`, `ResourceNotFoundException`).

---

## Variables de Entorno

La API es configurable a traves de variables de entorno para adaptarse a distintos entornos (Desarrollo, QA, Produccion):

| Variable | Descripcion | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL JDBC de conexion a PostgreSQL | `jdbc:postgresql://localhost:5433/wishlist_db` |
| `DB_USERNAME` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contrasena de la base de datos | `secret` |
| `JWT_SECRET` | Clave secreta para firmar los tokens JWT (minimo 32 caracteres) | Generar con `openssl rand -base64 32` |

En Docker Compose, estas variables se pasan directamente como variables de entorno al contenedor del backend.

---

## Despliegue en Render

1. Crear un servicio Web en Render conectado al repositorio.
2. Configurar las **Environment Variables** en Render:
   - `DB_URL`: URL de la base de datos PostgreSQL de Render (formato `jdbc:postgresql://...`)
   - `DB_USERNAME`: Usuario de la BD
   - `DB_PASSWORD`: Contrasena de la BD
   - `JWT_SECRET`: Secreto JWT (generar con `openssl rand -base64 32`)
3. Configurar los comandos en el servicio de Render:
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/*.jar`

**IMPORTANTE:** Nunca colocar credenciales reales en el repositorio. Todas las credenciales deben configurarse como Environment Variables en Render.

---

## Documentacion de la API (Swagger)

El sistema autogenera su propio manual interactivo usando el estandar OpenAPI v3.

- **Interfaz Grafica (Swagger UI):** `/swagger-ui/index.html`
- **Esquema JSON (OpenAPI):** `/v3/api-docs`

---

## Contribuidores

- **Janner Escobar** - Backend Developer (Modulo Product, Documentacion Swagger, Core de Validaciones, Tests, Docker).
- **Michael Vera** - Backend Developer (Seguridad JWT, Modulo Users, Modulo Wishlist, Contenedores).
