# Gestor de solicitudes: proyecto base del curso

Aplicación de ejemplo del curso *Desarrollo de aplicaciones seguras y de calidad con Spring Boot y Angular*.
Permite iniciar sesión, listar solicitudes, consultar su detalle, darlas de alta y, con rol ADMIN, cambiar su estado.

```text
Angular (http://localhost:4200)  --/api-->  Spring Boot (http://localhost:8080)  -->  H2 (fichero backend/data)
```

## Puntos de partida por sesión

Cada sesión con código termina en una etiqueta de Git. Si en una sesión algo no funciona, se puede continuar desde la etiqueta de la sesión anterior:

```bash
git switch -c mi-trabajo fin-sesion-03
```

Esto crea una rama propia (`mi-trabajo`) a partir de ese punto. Los cambios que no se hayan guardado con commit hay que guardarlos o descartarlos antes.

| Etiqueta | Estado del proyecto al terminar la sesión |
|---|---|
| `fin-sesion-01` | Proyectos Spring Boot y Angular generados, endpoint de saludo y consola H2 |
| `fin-sesion-03` | API REST con controlador, servicio, DTO y validación; datos en memoria |
| `fin-sesion-04` | Persistencia con JPA y H2; tratamiento centralizado de errores |
| `fin-sesion-05` | Pruebas unitarias, regla de negocio, secretos fuera del código y errores seguros |
| `fin-sesion-06` | Angular: componente con lista local, `@for` con `@empty`, *binding* y eventos |
| `fin-sesion-07` | Angular conectado a la API con `HttpClient`: listado, detalle y rutas |
| `fin-sesion-08` | Formulario reactivo de alta con validación y errores del servidor |
| `fin-sesion-09` | Seguridad: login con JWT, roles, autorización por recurso y demos (estado final) |

Las sesiones 2 (arquitectura) y 10 (integración y evaluación) no añaden código: la 2 parte de `fin-sesion-01` y la 10 de `fin-sesion-09`.

## Versiones fijadas

| Herramienta | Versión |
|---|---|
| Java (JDK) | 21 (probado con Eclipse Temurin 21.0.12) |
| Spring Boot | 4.1.1 (Maven se descarga solo con `mvnw`) |
| Node.js | 24 LTS (probado con 24.21.0) |
| Angular | 22.1 (se instala con `npm install`, no hace falta Angular CLI global) |
| Base de datos | H2 embebida, no requiere instalación |

Comprobar antes de empezar:

```bash
java -version
node -v
```

## Arrancar la aplicación

Hacen falta **dos terminales**, una para cada parte.

**1. Backend** (carpeta `backend`):

```bash
mvnw spring-boot:run
```

En PowerShell: `.\mvnw spring-boot:run`. Está listo cuando aparece `Started GestorSolicitudesApplication`.
Comprobación: <http://localhost:8080/api/saludo> devuelve un mensaje JSON.

**2. Frontend** (carpeta `frontend`), la primera vez:

```bash
npm install
```

y después, siempre:

```bash
npm start
```

Abrir <http://localhost:4200>. Las llamadas a `/api` se redirigen al backend mediante `proxy.conf.json`.

## Usuarios de prueba

| Usuario | Contraseña | Rol | Puede |
|---|---|---|---|
| ana | ana123 | USUARIO | Ver y crear sus propias solicitudes |
| luis | luis123 | USUARIO | Ver y crear sus propias solicitudes |
| admin | admin123 | ADMIN | Ver todas y cambiar su estado |

Son usuarios en memoria, definidos en `SeguridadConfig.java`. Solo sirven para el curso.

## API

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| GET | `/api/saludo` | Público | 200 |
| POST | `/api/auth/login` | Público | 200 con token, 400, 401 |
| GET | `/api/solicitudes` | USUARIO, ADMIN | 200 (USUARIO solo recibe las suyas), 401 |
| GET | `/api/solicitudes/{id}` | USUARIO, ADMIN | 200, 401, 403 si es ajena, 404 |
| POST | `/api/solicitudes` | USUARIO, ADMIN | 201, 400, 401 |
| PATCH | `/api/solicitudes/{id}/estado` | ADMIN | 200, 400, 401, 403, 404 |

Los errores siguen el formato estándar *ProblemDetail* y nunca incluyen trazas internas.
La colección `postman/gestor-solicitudes.postman_collection.json` tiene todas las peticiones: se importa en Postman, se ejecuta un login y el token se guarda automáticamente.

## Estructura

```text
backend/src/main/java/es/curso/solicitudes/
  controller/   Capa web: recibe HTTP y devuelve JSON
  service/      Lógica de negocio y reglas de autorización por recurso
  repository/   Acceso a datos con Spring Data JPA
  model/        Entidades JPA (tablas)
  dto/          Datos de entrada y salida de la API
  error/        Tratamiento centralizado de errores
  config/       Seguridad y datos iniciales
  demo/         Código vulnerable A PROPÓSITO (solo con el perfil demo-inseguro)

frontend/src/app/
  auth/         Login y sesión (token JWT)
  solicitudes/  Modelo, servicio HTTP y pantallas de listado, detalle y alta
```

## Pruebas

- Backend: `mvnw test`. Pruebas unitarias del servicio y de validación y permisos del controlador.
- Frontend: `npm test`.

## Base de datos H2

- Los datos se guardan en `backend/data`. **Para volver a los datos iniciales, parar el backend y borrar esa carpeta.**
- Consola web: <http://localhost:8080/h2-console>, con JDBC URL `jdbc:h2:file:./data/solicitudes`, usuario `sa` y contraseña vacía.

## Demostraciones de seguridad (sesión 9)

Arrancar el backend con el perfil que activa los endpoints vulnerables de `/api/demo`:

```bash
mvnw spring-boot:run -Dspring-boot.run.profiles=demo-inseguro
```

| Demo | Endpoint vulnerable | Versión correcta |
|---|---|---|
| Inyección SQL | `GET /api/demo/buscar-inseguro?texto=%' OR 1=1 --` devuelve solicitudes de todos | `buscar-seguro` usa parámetros y no devuelve nada |
| IDOR | `GET /api/demo/solicitudes/1` con el token de luis enseña una solicitud de ana | `GET /api/solicitudes/1` responde 403 |
| Validación solo en cliente | `POST /api/demo/solicitudes` con título vacío se guarda | `POST /api/solicitudes` responde 400 |

Sin ese perfil, los endpoints de demostración no existen. **No usar nunca ese perfil fuera del aula.**

## Configuración y secretos

Los valores sensibles se leen de variables de entorno. Si no existen, se usa un valor por defecto que solo es válido en el curso:

| Variable | Uso |
|---|---|
| `JWT_SECRETO` | Clave de firma de los tokens (mínimo 32 caracteres) |
| `DB_PASSWORD` | Contraseña de H2 (vacía por defecto) |

## Problemas frecuentes

| Síntoma | Causa probable y solución |
|---|---|
| `Database may be already in use` al arrancar el backend (el último error dice `Unable to determine Dialect`) | Ya hay otro backend arrancado **en la misma carpeta**, o se están ejecutando las pruebas. Cerrarlo. |
| `Port 8080 was already in use` / `Port 4200 is already in use` | Hay otro programa (u otro backend de otra carpeta) usando el puerto. Cerrarlo o reiniciar la terminal. |
| `Unable to establish loopback connection` al arrancar el backend | Restricción de Windows o del antivirus sobre la carpeta temporal. Arrancar con `mvnw spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=C:\temp"` (la carpeta debe existir). |
| `release version 21 not supported` | `mvnw` está usando otro JDK. Revisar `JAVA_HOME` y `java -version`. |
| `The Angular CLI requires a minimum Node.js version` | Node demasiado antiguo. Instalar Node 24 LTS. |
| El listado dice "¿Está arrancada la API?" | El backend no está arrancado o no ha terminado de arrancar. |
| El listado pide iniciar sesión tras un rato | El token caduca a los 60 minutos. Volver a entrar. |
| Aviso de Mockito "self-attaching" al pasar las pruebas | Aviso informativo de Java 21. No afecta. |
| `npm install` falla por red o proxy corporativo | Configurar el proxy de npm (`npm config set proxy ...`) o usar la carpeta `node_modules` que proporcione el formador. |
