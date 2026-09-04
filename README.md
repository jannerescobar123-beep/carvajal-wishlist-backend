<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x+-green.svg" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue.svg" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED.svg" alt="Docker Ready" />
  <img src="https://img.shields.io/badge/JWT-Security-red.svg" alt="JWT Security" />
</div>

<h1 align="center">🎁 Sistema de Lista de Deseos (Wishlist API) - Carvajal</h1>

<p align="center">
  API RESTful robusta y escalable diseñada para gestionar la autenticación de usuarios, perfiles y el sistema central de Listas de Deseos. Desarrollada con <b>Spring Boot</b> y empaquetada en <b>Docker</b>.
</p>

---

## 📖 Índice
- [Características Principales](#-características-principales)
- [Arquitectura y Tecnologías](#-arquitectura-y-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración y Despliegue](#-configuración-y-despliegue)
  - [Opción 1: Docker (Recomendado)](#opción-1-docker-compose-recomendado)
  - [Opción 2: Ejecución Local](#opción-2-ejecución-local-sin-docker)
- [Variables de Entorno](#-variables-de-entorno)
- [Documentación de la API (Swagger)](#-documentación-de-la-api)
- [Estructura de Endpoints](#-estructura-de-endpoints)
- [Flujo de Trabajo (GitFlow)](#-flujo-de-trabajo-gitflow)
- [Contribuidores](#-contribuidores)

---

## ✨ Características Principales
- **Autenticación Segura (Stateless):** Implementación completa de JSON Web Tokens (JWT) con soporte para roles (`ADMIN`, `CLIENT`).
- **Gestión de Usuarios:** Registro seguro con contraseñas cifradas vía `BCrypt`.
- **Lista de Deseos:** Endpoints para agregar, listar, ver el histórico y remover productos.
- **Validación de Inventario:** Integración en tiempo real para verificar el stock del producto al interactuar con la lista.
- **Protección CORS Configurada:** Lista para integrarse de inmediato con clientes Frontend (Ej. Angular en `localhost:4200`).
- **Manejo Global de Errores:** Excepciones interceptadas y presentadas en un formato JSON estándar y amigable.

---

## 🛠 Arquitectura y Tecnologías
- **Lenguaje:** Java 21
- **Framework:** Spring Boot (MVC, Data JPA, Security)
- **Base de Datos:** PostgreSQL 16
- **Seguridad:** Spring Security + `io.jsonwebtoken`
- **Documentación:** Springdoc OpenAPI (Swagger UI)
- **Contenedores:** Docker & Docker Compose

---

## 📋 Requisitos Previos
Para ejecutar y colaborar en el proyecto, asegúrate de tener instalado:
- [Java 21 JDK](https://adoptium.net/) (si se ejecuta localmente).
- [Apache Maven 3.8+](https://maven.apache.org/) (opcional, el proyecto incluye `mvnw`).
- [Docker](https://www.docker.com/products/docker-desktop) y [Docker Compose](https://docs.docker.com/compose/).
- [Git](https://git-scm.com/).

---

## 🚀 Configuración y Despliegue

### Opción 1: Docker Compose (Recomendado)
Esta es la forma más rápida de levantar todo el ecosistema (Base de Datos + Backend) con un solo comando.

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-organizacion/carvajal-wishlist-backend.git
   cd carvajal-wishlist-backend
   ```
2. Ejecuta el entorno con Docker Compose:
   ```bash
   docker-compose up --build
   ```
3. La API estará disponible en `http://localhost:8080`.

### Opción 2: Ejecución Local (Sin Docker)
Si prefieres correr la app directamente en tu máquina:

1. Asegúrate de tener una instancia de **PostgreSQL** corriendo en el puerto `5432` con una base de datos llamada `wishlist_db`.
2. Opcionalmente, configura las credenciales ajustando el archivo `src/main/resources/application.properties` o inyectando variables de entorno.
3. Ejecuta el wrapper de Maven:
   ```bash
   ./mvnw clean spring-boot:run
   ```

---

## ⚙️ Variables de Entorno

El proyecto soporta inyección de variables para despliegues dinámicos. Si usas Docker Compose, puedes definir un archivo `.env` o exportarlas en tu consola:

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL | `jdbc:postgresql://localhost:5432/wishlist_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la BD | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la BD | `postgres` |
| `JWT_SECRET` | Clave secreta para firmar los tokens JWT (mínimo 32 bytes) | `mi_secreto_seguro_1234567890_minimo_32_bytes_para_hmac` |

---

## 📚 Documentación de la API

El proyecto incluye auto-documentación interactiva. Una vez que la aplicación esté corriendo, visita:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🔌 Estructura de Endpoints

### 🔐 Autenticación (Públicos)
- `POST /api/auth/register` - Registra un nuevo usuario (`CLIENT`).
- `POST /api/auth/login` - Valida credenciales y retorna el JWT Token.

### 🛒 Lista de Deseos (Requiere Token `CLIENT`)
- `GET /api/wishlist` - Obtiene los productos activos en la lista del usuario, validando su stock.
- `POST /api/wishlist` - Agrega un producto a la lista (Requiere `{ productId, quantity }`).
- `DELETE /api/wishlist/{productId}` - Elimina un producto específico de la lista.
- `GET /api/wishlist/history` - Lista el histórico de interacciones ordenado por fecha.

### 👑 Administración (Requiere Token `ADMIN`)
- `PUT /api/admin/users/{userId}/role` - Actualiza el rol de un usuario.

### 📦 Productos (Públicos)
- `GET /api/products` - Lista de productos del catálogo.

---

## 🔀 Flujo de Trabajo (GitFlow)

Este repositorio opera bajo una estricta filosofía **GitFlow**. Por favor sigue las convenciones al contribuir:

1. **Ramas Principales:**
   - `main`: Entorno de Producción. Siempre estable.
   - `develop-dev`: Entorno de Integración / Staging.
2. **Ramas de Trabajo:**
   - Funcionalidades nuevas: `feature/nombre-de-la-funcionalidad`
   - Ramas personales: `NombreApellidoDev` (Ej. `MichaelVeraDev`)
3. **Convención de Commits:** Usar [Conventional Commits](https://www.conventionalcommits.org/):
   - `feat(...)`: Para nuevas características.
   - `fix(...)`: Para solución de bugs.
   - `docs(...)`: Cambios en el README o documentación.

---

## 👥 Contribuidores

- **[Janner Escobar]** - Backend Developer (Modelo Product, Swagger, Validaciones).
- **[Michael Vera]** - Backend Developer (Seguridad JWT, Users, Lista de Deseos, Dockerización).

---
*Hecho con ❤️ para Carvajal*
