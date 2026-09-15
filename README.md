<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.4.1-green.svg" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue.svg" alt="PostgreSQL 16" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED.svg" alt="Docker Ready" />
  <img src="https://img.shields.io/badge/JWT-Stateless-red.svg" alt="JWT Security" />
  <img src="https://img.shields.io/badge/Tests-61%20Passed-brightgreen.svg" alt="Tests 61 Passed" />
</div>

<h1 align="center">🛒 Carvajal Wishlist API - Microservicio B2C</h1>

<p align="center">
  <b>Solución Oficial - Prueba Técnica Desarrollador Expert</b><br>
  Microservicio backend RESTful para la gestión del catálogo de productos y el sistema de Listas de Deseos (Wishlist) con validación de inventario en tiempo real, trazabilidad histórica y seguridad perimetral JWT.
</p>

---

## 📖 Índice General
- [🚀 Despliegue Rápido (Manual del Evaluador)](#-despliegue-rápido-manual-del-evaluador)
- [🔑 Credenciales Preconfiguradas](#-credenciales-preconfiguradas)
- [🗄️ Modelo y Scripts de Base de Datos](#️-modelo-y-scripts-de-base-de-datos)
- [🎨 Guía de Integración para el Equipo Frontend](#-guía-de-integración-para-el-equipo-frontend)
- [📡 Referencia Completa de Endpoints](#-referencia-completa-de-endpoints)
- [🧪 Ejecución de Pruebas Automatizadas](#-ejecución-de-pruebas-automatizadas)
- [🌐 Demostración en Vivo (Cloud Railway)](#-demostración-en-vivo-cloud-railway)
- [👥 Contribuidores](#-contribuidores)

---

## 🚀 Despliegue Rápido (Manual del Evaluador)

El microservicio está 100% contenerizado y preparado para ejecutarse de forma agnóstica en cualquier entorno.

### Opción A: Despliegue Automatizado con Docker Compose (Recomendada)
Requiere **Docker** y **Docker Compose** instalados:

```bash
# 1. Clonar el repositorio y ubicarse en la raíz
git clone https://github.com/jannerescobar123-beep/carvajal-wishlist-backend.git
cd carvajal-wishlist-backend

# 2. Iniciar PostgreSQL y la API en un solo comando
JWT_SECRET='clave_secreta_para_desarrollo_carvajal_2026_min_32_chars' docker-compose up -d --build
```
*La API estará disponible de inmediato en `http://localhost:8080` y la base de datos en el puerto `5432`.*

---

### Opción B: Ejecución Local con Maven (Java 21)
Requiere **Java 21 LTS** y una instancia local de PostgreSQL activa:

```bash
# Exportar variables requeridas
export DB_URL='jdbc:postgresql://localhost:5432/wishlist_db'
export DB_USERNAME='postgres'
export DB_PASSWORD='tu_password'
export JWT_SECRET='clave_secreta_para_desarrollo_carvajal_2026_min_32_chars'
export SPRING_PROFILES_ACTIVE='dev'

# Compilar y arrancar la aplicación
./mvnw spring-boot:run
```

---

## 🔑 Credenciales Preconfiguradas

Para agilizar las pruebas funcionales del evaluador, se suministran dos cuentas de usuario predeterminadas:

| Rol | Usuario | Correo | Contraseña | Permisos |
| :--- | :--- | :--- | :--- | :--- |
| 🛡️ **ADMIN** | `admin_janner` | `admin@carvajal.com` | `superpassword123` | Gestión de catálogo, actualización de roles y auditoría. |
| 👤 **CLIENT** | `cliente_prueba` | `cliente@carvajal.com` | `superpassword123` | Consulta de catálogo, gestión de lista de deseos e historial. |

---

## 🗄️ Modelo y Scripts de Base de Datos

- **Motor Seleccionado:** PostgreSQL 16 (Garantías ACID, control estricto de concurrencia e integridad referencial).
- **Documento Formal del Modelo de Datos:** Consulte [`docs/DATABASE_MODEL.md`](docs/DATABASE_MODEL.md) para ver el diagrama Entidad-Relación (Mermaid), el diccionario de datos y las justificaciones de arquitectura.
- **Script Semilla (Seed Data):** Ubicado en [`scripts/seed-data.sql`](scripts/seed-data.sql). Contiene:
  - 10 productos Carvajal con stock suficiente y un ítem deliberadamente agotado para validar la regla de notificación **RF-3**.
  - Los 2 usuarios de prueba con contraseñas cifradas en BCrypt.

---

## 🎨 Guía de Integración para el Equipo Frontend

### 1. Documentación Interactiva y OpenAPI
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Contrato OpenAPI v3:** `http://localhost:8080/v3/api-docs`

### 2. Flujo de Autenticación (JWT Stateless)
1. **Login:** Realizar `POST /api/auth/login` enviando `{"username": "...", "password": "..."}`.
2. **Token:** Extraer el campo `token` de la respuesta JSON.
3. **Inyección en Cabeceras:** Enviar el token en todas las rutas protegidas:
   ```http
   Authorization: Bearer <TU_JWT_TOKEN>
   ```

### 3. Regla de Negocio: Notificación de Stock Agotado (RF-3)
Al consultar la lista del cliente (`GET /api/wishlist`), el backend evalúa el inventario en tiempo real y retorna:
```json
{
  "items": [
    {
      "productId": 9,
      "productName": "Borrador Nata Pelikan (Agotado)",
      "quantity": 1,
      "price": 1500.00,
      "inStock": false
    }
  ],
  "hasOutOfStockItems": true,
  "notificationMessage": "Algunos productos de tu lista de deseos ya no cuentan con stock disponible."
}
```
*El Frontend debe utilizar `hasOutOfStockItems` y `notificationMessage` para emitir una alerta en pantalla y deshabilitar los botones de compra de los artículos con `inStock: false`.*

### 4. Estructura Estándar de Errores (RFC 7807)
Todos los errores de negocio (400, 401, 403, 404, 409) son interceptados globalmente por [`GlobalExceptionHandler.java`](src/main/java/com/carvajal/wishlist/exception/GlobalExceptionHandler.java):
```json
{
  "timestamp": "2026-09-15T10:05:00.123",
  "status": 409,
  "error": "Conflict",
  "message": "El producto ya se encuentra en tu lista de deseos"
}
```

### 5. Configuración de CORS
El backend autoriza por defecto el origen de Angular local (`http://localhost:4200`) y el despliegue en Vercel (`https://carvajal-frontend-m.vercel.app`). Para orígenes adicionales o alternativos (ej. React 3000 / Vite 5173), definir la variable de entorno separando por comas:
```bash
export CORS_ALLOWED_ORIGIN="http://localhost:4200,https://carvajal-frontend-m.vercel.app,http://localhost:3000"
```

---

## 📡 Referencia Completa de Endpoints

| Verbo | Endpoint | Descripción | Autorización Requerida |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/auth/register` | Registro de nuevos usuarios | Público |
| `POST` | `/api/auth/login` | Autenticación y generación de JWT | Público |
| `GET` | `/api/products` | Catálogo de productos con stock en tiempo real | Público |
| `GET` | `/api/products/{id}` | Detalle de un producto por ID | Público |
| `POST` | `/api/products` | Crear nuevo producto en catálogo | 🛡️ `ROLE_ADMIN` |
| `PUT` | `/api/products/{id}` | Actualizar producto | 🛡️ `ROLE_ADMIN` |
| `DELETE` | `/api/products/{id}` | Desactivar / Eliminar producto | 🛡️ `ROLE_ADMIN` |
| `GET` | `/api/wishlist` | Consultar lista con validación de stock y notificación | 🛡️ `ROLE_CLIENT` |
| `POST` | `/api/wishlist` | Agregar producto a la lista de deseos | 🛡️ `ROLE_CLIENT` |
| `PUT` | `/api/wishlist/{productId}` | Actualizar cantidad de un ítem en lista | 🛡️ `ROLE_CLIENT` |
| `DELETE` | `/api/wishlist/{productId}` | Remover producto de la lista | 🛡️ `ROLE_CLIENT` |
| `GET` | `/api/wishlist/history` | Historial inmutable de movimientos del usuario | 🛡️ `ROLE_CLIENT` |
| `PUT` | `/api/admin/users/{userId}/role` | Promover / Asignar rol a un usuario | 🛡️ `ROLE_ADMIN` |

---

## 🧪 Ejecución de Pruebas Automatizadas

La suite incluye 61 pruebas unitarias y de integración que validan seguridad, controladores WebMvc, servicios Mockito y persistencia H2:

```bash
# Ejecutar la suite completa con Java 21 LTS
./mvnw test
```

**Resultado:** `Tests run: 61, Failures: 0, Errors: 0, Skipped: 0 - BUILD SUCCESS`

---

## 🌐 Demostración en Vivo (Cloud Railway)

El microservicio se encuentra desplegado y activo en la nube:
- **Swagger UI en Producción:** [https://carvajal-wishlist-backend-production.up.railway.app/swagger-ui/index.html](https://carvajal-wishlist-backend-production.up.railway.app/swagger-ui/index.html)
- **Base URL API:** `https://carvajal-wishlist-backend-production.up.railway.app/api`

---

## 👥 Contribuidores

- **Janner Escobar** - Backend Developer & Solutions Architect
- **Michael Vera** - Security & Resilience Architect (Spring Security 6, JWT, JPA Resilience)
