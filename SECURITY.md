# Guía de Seguridad - Carvajal Wishlist API

## Configuración Inicial

### Variables de Entorno Requeridas

La aplicación requiere las siguientes variables de entorno para funcionar:

```bash
# JWT Secret (OBLIGATORIO - Mínimo 32 caracteres)
export JWT_SECRET='your-secret-key-min-32-chars-here-change-in-production'

# Base de datos PostgreSQL
export DB_URL='jdbc:postgresql://localhost:5433/wishlist_db'
export DB_USERNAME='postgres'
export DB_PASSWORD='admin123'
```

### Validación del Secreto JWT

El secreto JWT se valida automáticamente al iniciar la aplicación en la clase `JwtUtil.java#@PostConstruct`:

✅ **Requisitos:**
- Debe estar definido (no puede ser vacío)
- Debe tener al menos 32 caracteres
- Se usa para firmar/validar tokens HS256

❌ **Si no cumple**, la aplicación falla con error claro:
```
IllegalArgumentException: JWT_SECRET must be at least 32 characters long...
```

---

## Autenticación con JWT

### Flujo de Autenticación

#### 1. Registro de Usuario
```bash
POST /api/auth/register
Content-Type: application/json

{
    "username": "newuser",
    "email": "user@example.com",
    "password": "securepassword123",
    "role": "CLIENT"  # CLIENT o ADMIN
}

Response: 201 Created
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "newuser"
}
```

#### 2. Login
```bash
POST /api/auth/login
Content-Type: application/json

{
    "username": "newuser",
    "password": "securepassword123"
}

Response: 200 OK
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "newuser"
}
```

#### 3. Usar el Token en Requests Autenticados
```bash
GET /api/products/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response: 200 OK
{
    "id": 1,
    "name": "Laptop",
    ...
}
```

### Estructura del JWT

El token JWT contiene:
- **Header**: `{"alg": "HS256", "typ": "JWT"}`
- **Payload**: 
  ```json
  {
    "sub": "username",
    "roles": ["CLIENT"],
    "iat": 1694184000,
    "exp": 1694270400
  }
  ```
- **Signature**: Firmado con `JWT_SECRET` usando HS256

---

## Autorización por Endpoint

### Rutas Públicas (Sin Autenticación)

```
GET  /api/products         - Listar todos los productos
GET  /api/products/{id}    - Obtener un producto específico
GET  /swagger-ui/**        - Documentación de API
GET  /v3/api-docs/**       - OpenAPI JSON
POST /api/auth/register    - Registrar nuevo usuario
POST /api/auth/login       - Iniciar sesión
```

### Rutas Privadas (Requieren JWT)

#### Usuarios (Autenticación Requerida)
```
GET    /api/users/{id}     - Obtener perfil del usuario
PUT    /api/users/{id}     - Actualizar perfil
DELETE /api/users/{id}     - Eliminar usuario
```

#### Productos (Requieren rol ADMIN)
```
POST   /api/products       - Crear producto
PUT    /api/products/{id}  - Actualizar producto
DELETE /api/products/{id}  - Eliminar producto
```

#### Wishlist (Requieren autenticación)
```
GET    /api/wishlist              - Obtener wishlist del usuario
POST   /api/wishlist              - Agregar producto a wishlist
DELETE /api/wishlist/{productId}  - Remover de wishlist
```

---

## Configuración por Ambiente

### Desarrollo (application.properties)

```properties
spring.application.name=carvajal-wishlist-backend
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5433/wishlist_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:admin123}
spring.jpa.hibernate.ddl-auto=update

# JWT - debe configurarse via ENV var
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000  # 24 horas
```

### Testing (application-test.properties)

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# JWT para testing
jwt.secret=test-secret-key-that-is-long-enough-for-testing-purposes-32chars
jwt.expiration=3600000  # 1 hora
```

### Producción

**Importante:** Cambiar los siguientes valores en env vars:
```bash
# Generar un secreto aleatorio robusto
export JWT_SECRET='$(openssl rand -base64 32)'

# Cambiar a base de datos de producción
export DB_URL='jdbc:postgresql://prod-db-host:5432/wishlist_db'
export DB_USERNAME='prod-user'
export DB_PASSWORD='prod-secure-password'
```

---

## Mejores Prácticas

### 1. Manejo de Secretos
- ✅ Usar variables de entorno (nunca commits en git)
- ✅ Generar un secreto nuevo por ambiente
- ✅ Usar secretos de al menos 32 caracteres
- ✅ Rotar secretos periódicamente
- ❌ No poner secretos en `application.properties`

### 2. Seguridad del Token
- ✅ Transmitir siempre por HTTPS
- ✅ Almacenar en memoria o sessionStorage (no localStorage)
- ✅ Incluir en header `Authorization: Bearer <token>`
- ✅ No exponer token en logs
- ❌ No pasar token en URL query params

### 3. Vencimiento y Refresh
- El token actual vence en **24 horas**
- Implementar refresh tokens para sesiones largas (future)
- Monitorear expiración del lado del cliente

### 4. Contraseñas
- Se codifican con BCrypt en la BD
- Al menos 8 caracteres recomendado
- No enviar contraseña en responses API
- Validar complejidad en registro

### 5. CORS
- Configurado para origen: `http://localhost:4200` (desarrollo)
- En producción, cambiar a dominios reales:
  ```java
  configuration.setAllowedOrigins(List.of("https://yourdomain.com"));
  ```

---

## Testing

### Ejecutar Tests Unitarios
```bash
# Asegurarse de que JWT_SECRET esté definido
export JWT_SECRET='test-secret-key-that-is-long-enough-for-testing-purposes-32chars'

# Ejecutar todos los tests
mvn test

# Ejecutar solo AuthIntegrationTest
mvn test -Dtest=AuthIntegrationTest

# Con cobertura
mvn test jacoco:report
```

### Tests con Autenticación
```java
// Test con usuario autenticado
@Test
@WithMockUser(username = "testuser", roles = "ADMIN")
void testAdminEndpoint() throws Exception {
    mockMvc.perform(post("/api/products"))
        .andExpect(status().isCreated());
}

// Test sin autenticación (debe fallar)
@Test
void testPrivateEndpointUnauthorized() throws Exception {
    mockMvc.perform(post("/api/products"))
        .andExpect(status().isUnauthorized());  // 401
}
```

---

## Troubleshooting

### Error: "JWT_SECRET must be at least 32 characters long"
**Causa:** JWT_SECRET no está configurado o es muy corto
**Solución:**
```bash
# Generar secreto de 32+ caracteres
export JWT_SECRET='your-secret-key-32-chars-minimum-here'
```

### Error: "Invalid or expired token" - 401 Unauthorized
**Causa:** Token inválido, expirado o malformado
**Solución:**
- Verificar que Authorization header es: `Bearer <token>`
- Obtener nuevo token con login
- Verificar que JWT_SECRET es el mismo en servidor

### Error: 403 Forbidden en endpoint protegido
**Causa:** Usuario no tiene rol requerido
**Solución:**
- Endpoint POST/PUT/DELETE requieren rol ADMIN
- Crear nuevo usuario con `"role": "ADMIN"`
- Verificar roles en token `/api/auth/login` response

### Los tests fallan sin JWT_SECRET
**Causa:** Tests requieren environment var
**Solución:**
```bash
export JWT_SECRET='test-secret-key-that-is-long-enough-for-testing-purposes-32chars'
mvn test
```

---

## Referencias

- [JWT.io - Debugger & Editor](https://jwt.io)
- [Spring Security Docs](https://docs.spring.io/spring-security/)
- [OWASP Auth Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [jjwt Library](https://github.com/jwtk/jjwt)

---

**Última Actualización:** 8 de septiembre de 2026
**Versión API:** 1.0.0
