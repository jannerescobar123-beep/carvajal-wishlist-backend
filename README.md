<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x+-green.svg" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue.svg" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED.svg" alt="Docker Ready" />
  <img src="https://img.shields.io/badge/JWT-Security-red.svg" alt="JWT Security" />
</div>

<h1 align="center">🎁 Sistema de Lista de Deseos (Wishlist API) - Carvajal</h1>

<p align="center">
  API RESTful robusta y escalable diseñada para gestionar la autenticación de usuarios, perfiles y el sistema central de Listas de Deseos para el ecosistema de comercio electrónico. Desarrollada con <b>Spring Boot</b> y diseñada con principios de alta cohesión y bajo acoplamiento.
</p>

---

## 📖 Índice
- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Características Principales](#-características-principales)
- [Arquitectura y Tecnologías](#-arquitectura-y-tecnologías)
- [Estructura de Endpoints](#-estructura-de-endpoints)
- [Reglas de Negocio](#-reglas-de-negocio)
- [Variables de Entorno](#-variables-de-entorno)
- [Documentación de la API (Swagger)](#-documentación-de-la-api-swagger)
- [Contribuidores](#-contribuidores)

---

## 🎯 Descripción del Proyecto

El **Sistema de Lista de Deseos** de Carvajal es un microservicio backend estratégico diseñado para potenciar la retención de clientes y facilitar las compras planificadas. 

Permite a los usuarios registrarse en la plataforma, explorar un catálogo de productos e ir guardando sus artículos favoritos en una lista de deseos personalizable. La API no solo almacena estos deseos, sino que interactúa en tiempo real con el inventario del negocio, previniendo que un usuario mantenga falsas expectativas sobre productos agotados y manteniendo un registro histórico inmutable de sus interacciones (agregar/remover) para futuros análisis de inteligencia de negocios o marketing.

---

## ✨ Características Principales
- **Autenticación Segura (Stateless):** Implementación completa de JSON Web Tokens (JWT) con soporte para roles (`ADMIN`, `CLIENT`).
- **Gestión de Usuarios:** Perfilado de usuarios con contraseñas cifradas vía `BCrypt` de Spring Security.
- **Lista de Deseos Dinámica:** Endpoints transaccionales para agregar, listar y remover productos del carrito de deseos.
- **Validación de Inventario en Tiempo Real:** Integración sincrónica para verificar que un producto cuente con stock físico antes y durante su permanencia en la lista de deseos.
- **Trazabilidad (Histórico):** Registro secuencial e histórico de interacciones del usuario con su lista.
- **Protección CORS Configurada:** Lista para integrarse de inmediato de forma segura con clientes Frontend (Ej. Angular).
- **Manejo Global de Errores:** Excepciones interceptadas (`@RestControllerAdvice`) y presentadas en un formato JSON estándar y predecible.

---

## 🛠 Arquitectura y Tecnologías
- **Lenguaje Core:** Java 21
- **Framework Principal:** Spring Boot (MVC, Data JPA, Security)
- **Capa de Persistencia:** PostgreSQL 16 (Entidades relacionales robustas)
- **Seguridad & Sesiones:** Spring Security + `io.jsonwebtoken`
- **Documentación de API:** Springdoc OpenAPI (Generación automática de Swagger UI)
- **Contenedores y Orquestación:** Docker & Docker Compose (Entorno encapsulado)

---

## 🔌 Estructura de Endpoints

### 🔐 Autenticación (Públicos)
- `POST /api/auth/register` - Registra un nuevo usuario con rol por defecto `CLIENT`.
- `POST /api/auth/login` - Valida credenciales contra la base de datos y retorna el JWT Token firmado.

### 🛒 Lista de Deseos (Requiere Token `CLIENT`)
- `GET /api/wishlist` - Obtiene los productos activos en la lista del usuario actual, anexando el estado de stock en tiempo real.
- `POST /api/wishlist` - Agrega un producto al carrito de deseos (Requiere `{ productId, quantity }`).
- `DELETE /api/wishlist/{productId}` - Elimina un producto específico de la lista.
- `GET /api/wishlist/history` - Lista el histórico de interacciones, ordenado de más reciente a más antiguo.

### 👑 Administración (Requiere Token `ADMIN`)
- `PUT /api/admin/users/{userId}/role` - Escala o degrada los permisos de un usuario existente.

### 📦 Productos (Públicos)
- `GET /api/products` - Lista de productos vigentes del catálogo (Módulo base).

---

## 🧠 Reglas de Negocio

El sistema aplica validaciones críticas mediante Excepciones personalizadas para proteger la integridad de los datos:
1. **Unicidad de Usuario:** No pueden existir dos cuentas con el mismo correo o nombre de usuario (`EmailAlreadyExistsException`, `UsernameAlreadyExistsException`).
2. **Duplicidad en Lista:** Un usuario no puede agregar el mismo producto más de una vez a su lista activa (`ProductAlreadyInWishlistException`).
3. **Disponibilidad (Stock):** Si el producto se encuentra inactivo o su stock es insuficiente (`quantity` > stock actual), la API denegará la adición a la lista de deseos (`StockNotAvailableException`, `ResourceNotFoundException`).

---

## ⚙️ Variables de Entorno

La API es configurable para adaptarse a distintos entornos (Desarrollo, QA, Producción):

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL JDBC de conexión a PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario administrador de la BD |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la BD |
| `JWT_SECRET` | Clave secreta para firmar los tokens JWT (Extrema seguridad en Producción) |

---

## 📚 Documentación de la API (Swagger)

El sistema autogenera su propio manual interactivo usando el estándar OpenAPI v3.

- **Interfaz Gráfica (Swagger UI):** `/swagger-ui.html`
- **Esquema JSON (OpenAPI):** `/v3/api-docs`

---

## 👥 Contribuidores

- **[Janner Escobar]** - Backend Developer (Módulo Product, Documentación Swagger, Core de Validaciones).
- **[Michael Vera]** - Backend Developer (Seguridad JWT, Módulo Users, Módulo Wishlist, Contenedores).
