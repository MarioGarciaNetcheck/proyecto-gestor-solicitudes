# Gestor de solicitudes: proyecto del curso

Aplicación de ejemplo del curso *Desarrollo de aplicaciones seguras y de calidad con Spring Boot y Angular*.

```text
Angular (http://localhost:4200)  --/api-->  Spring Boot (http://localhost:8080)  -->  H2 (fichero backend/data)
```

## Versiones

| Herramienta | Versión |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 4.1.1 (Maven se descarga solo con `mvnw`) |
| Node.js | 24 LTS |
| Angular | 22.1 (se instala con `npm install`, no hace falta Angular CLI global) |
| Base de datos | H2 embebida, no requiere instalación |

## Arrancar la aplicación

Hacen falta **dos terminales**, una para cada parte.

**1. Backend** (carpeta `backend`):

```bash
mvnw spring-boot:run
```

En PowerShell: `.\mvnw spring-boot:run`. Comprobación: <http://localhost:8080/api/saludo>.

**2. Frontend** (carpeta `frontend`), la primera vez:

```bash
npm install
```

y después, siempre:

```bash
npm start
```

Abrir <http://localhost:4200>.

## Puntos de partida por sesión

Cada sesión con código termina en una etiqueta `fin-sesion-NN`. Si en una sesión algo no funciona, se puede continuar desde la etiqueta de la sesión anterior:

```bash
git switch -c mi-trabajo fin-sesion-03
```

Esto crea una rama propia (`mi-trabajo`) a partir de ese punto. Los cambios que no se hayan guardado con commit hay que guardarlos o descartarlos antes.
