<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.4.1-green.svg" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue.svg" alt="PostgreSQL 16" />
  <img src="https://img.shields.io/badge/JWT-Security-red.svg" alt="JWT Security" />
</div>

<h1 align="center">🛒 Carvajal Wishlist API</h1>

<p align="center">
  <b>Documentación Oficial para Integración Frontend</b><br>
  API RESTful para gestionar la autenticación, el catálogo de productos escolares/oficina y las listas de deseos (Wishlist) de los clientes de Carvajal.
</p>

---

## 📖 Índice
- [Recursos de Integración](#-recursos-de-integración)
- [Flujo de Autenticación (JWT)](#-flujo-de-autenticación-jwt)
- [Guía de Integración: Wishlist](#-guía-de-integración-wishlist)
- [Manejo de Errores (Interceptors)](#-manejo-de-errores-interceptors)
- [Referencia de Endpoints](#-referencia-de-endpoints)
- [Políticas CORS](#-políticas-cors)

---

## 🔗 Recursos de Integración

Para facilitar el trabajo del equipo Frontend, la API cuenta con una interfaz interactiva donde puedes probar todas las rutas y autogenerar tus interfaces/tipos (ej. para TypeScript).

- **Swagger UI (Pruebas manuales):** `/swagger-ui/index.html`
- **Esquema OpenAPI (JSON):** `/v3/api-docs`

---

## 🔐 Flujo de Autenticación (JWT)

Esta API es **Stateless**. No utiliza cookies de sesión, todo se maneja a través de JSON Web Tokens (JWT).

### 1. Obtener el Token
El usuario debe iniciar sesión enviando sus credenciales a `POST /api/auth/login`.
```json
// Petición
{
  "username": "usuario_ejemplo",
  "password": "mi_password_secreto"
}

// Respuesta (200 OK)
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "usuario_ejemplo",
  "role": "CLIENT"
}
```

### 2. Inyectar el Token
Debes guardar el `token` (en `localStorage`, `sessionStorage` o Zustand/Redux). Para consultar cualquier ruta protegida (ej. la lista de deseos), debes enviar este token en los **Headers** de tu petición HTTP usando el esquema `Bearer`:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
*(Cualquier petición sin este header, o con un token expirado, devolverá un error `401 Unauthorized`).*

---

## 🛍️ Guía de Integración: Wishlist

Para construir una experiencia de usuario fluida, te recomendamos seguir este flujo en el Frontend:

1. **Mostrar el Catálogo:** Consume `GET /api/products` (Ruta pública, no requiere token). Muestra las tarjetas de productos.
2. **Botón "Añadir a Deseos":** Si el usuario hace clic y no tiene token, redirígelo a la vista de `/login`.
3. **Guardar en Wishlist:** Si está logueado, haz un `POST /api/wishlist` con el siguiente cuerpo:
   ```json
   {
     "productId": 5,
     "quantity": 1
   }
   ```
4. **Renderizar la Wishlist:** Consume `GET /api/wishlist` para pintar el carrito de deseos. 
   > 💡 **Tip de UI:** La respuesta de este endpoint incluye la propiedad booleana `inStock`. Si un usuario guardó un producto que posteriormente se agotó, esta propiedad vendrá en `false`. **Usa este valor para deshabilitar el botón de "Comprar" visualmente en tu interfaz.**

---

## ⚠️ Manejo de Errores (Interceptors)

Cualquier error de negocio o validación (400, 401, 403, 404, 409) devolverá **siempre** esta estructura JSON canónica. 

**Recomendación:** Configura un *Interceptor* global en tu cliente HTTP (Axios, Fetch, Angular HttpClient) para leer siempre el campo `message` y mostrarlo en un Toast/Alerta al usuario.

```json
{
  "timestamp": "2026-09-11T15:05:55.441",
  "status": 400,
  "error": "Bad Request",
  "message": "No hay suficiente stock disponible para este producto"
}
```

### Errores Comunes a Capturar
- `401 Unauthorized`: Token ausente o expirado. (Acción sugerida: Desloguear y redirigir al Login).
- `409 Conflict`: Intentas registrar un usuario que ya existe, o agregar un producto que ya está en la Wishlist.
- `404 Not Found`: El producto solicitado no existe o fue deshabilitado.

---

## 📡 Referencia de Endpoints

| Método | Endpoint | Descripción | Requiere Auth |
| :--- | :--- | :--- | :---: |
| **POST** | `/api/auth/register` | Crea una nueva cuenta de cliente | ❌ |
| **POST** | `/api/auth/login` | Autentica y devuelve el JWT | ❌ |
| **GET** | `/api/products` | Lista el catálogo de productos activos | ❌ |
| **GET** | `/api/products/{id}` | Detalle de un producto específico | ❌ |
| **GET** | `/api/wishlist` | Obtiene la lista de deseos del usuario actual | 🛡️ `CLIENT` |
| **POST** | `/api/wishlist` | Añade un producto a la lista | 🛡️ `CLIENT` |
| **DELETE**| `/api/wishlist/{productId}` | Remueve un producto de la lista | 🛡️ `CLIENT` |
| **GET** | `/api/wishlist/history` | Historial inmutable de movimientos | 🛡️ `CLIENT` |

---

## 🌐 Políticas CORS

Por motivos de seguridad, la API rechaza peticiones desde orígenes no autorizados. 
- Por defecto, el tráfico está permitido desde `http://localhost:4200` (entorno Angular por defecto). 
- **Si usas React (3000) o Vite (5173):** Asegúrate de notificar al desarrollador backend para que modifique la variable de entorno `CORS_ALLOWED_ORIGIN` en el servidor, de lo contrario verás un error rojo de "CORS Policy" en la consola de tu navegador.

---
*Desarrollado con ❤️ por el equipo de Backend de Carvajal (Janner Escobar & Michael Vera).*
