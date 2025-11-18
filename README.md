# jpasseratplp32025

Proyecto Spring Boot para gestión de personas y empleados (herencia JPA, validaciones y endpoints REST).

## Requisitos

- Java 21
- Maven (o usar `./mvnw` / `mvnw.cmd`)
- Puerto por defecto: 8080

## Ejecutar la aplicación

1. Compilar y ejecutar con Maven wrapper:

```bash
./mvnw spring-boot:run
```

o con Maven instalado:

```bash
mvn spring-boot:run
```


2. Clase principal: [py.edu.uc.jpasseratplp32025.Jpasseratplp32025Application](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)   
3. Configuración de BD/H2: [application.properties](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

## Arquitectura (resumen)

- Entidad base (Single Table Inheritance): [py.edu.uc.jpasseratplp32025.entity.PersonaJpa](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - Subclases:
        - [py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
        - [py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
        - [py.edu.uc.jpasseratplp32025.entity.Contratista](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
- Repositorios JPA:
    - [py.edu.uc.jpasseratplp32025.repository.PersonaRepository](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - Repositorios específicos (ej. [EmpleadoTiempoCompletoRepository](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html))
- Servicios:
    - Lógica y validaciones: [py.edu.uc.jpasseratplp32025.service.PersonaService](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - Servicio para EmpleadoTiempoCompleto con batch y validaciones: [py.edu.uc.jpasseratplp32025.service.EmpleadoTiempoCompletoService](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - Otros: [NominaService](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [RemuneracionesService](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
- Controladores REST:
    - Personas: [py.edu.uc.jpasseratplp32025.controller.PersonaController](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - Empleados: [py.edu.uc.jpasseratplp32025.controller.EmpleadoTiempoCompletoController](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - Contratistas: [py.edu.uc.jpasseratplp32025.controller.ContratistaController](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - Remuneraciones: [py.edu.uc.jpasseratplp32025.controller.RemuneracionesController](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
- Manejo global de errores: [py.edu.uc.jpasseratplp32025.exception.GlobalExceptionHandler](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) y DTO de errores [ErrorResponseDto](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
- DTOs de respuesta: [EmpleadoTiempoCompletoImpuestoDto](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [BaseDto](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), etc.

Notas:

- Se utiliza estrategia JPA SINGLE_TABLE ([PersonaJpa](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)) — algunas columnas de subclases están marcadas [nullable = true](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) para evitar errores de integridad cuando la fila corresponde a otra subclase.
- Validaciones: Jakarta Bean Validation + validaciones de negocio en los métodos [validarDatosEspecificos()](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) de cada entidad.
- Excepción de negocio para fecha de nacimiento futura: [py.edu.uc.jpasseratplp32025.exception.FechaNacimientoFuturaException](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

## Endpoints principales y ejemplos cURL

Nota: usar header `Content-Type: application/json`. Fechas en formato ISO `yyyy-MM-dd`.

1. Crear una persona genérica (POST /api/personas)

```bash
curl -X POST http://localhost:8080/api/personas \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Juan",
    "apellido":"Pérez",
    "fechaDeNacimiento":"1990-01-15",
    "numeroDeCedula":"1234567"
  }'
```

Controlador: [py.edu.uc.jpasseratplp32025.controller.PersonaController](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

2. Crear empleado de tiempo completo (POST /api/empleados)

```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Luis",
    "apellido":"López",
    "fechaDeNacimiento":"1993-01-22",
    "numeroDeCedula":"63890123",
    "salarioMensual":3000000,
    "departamento":"Marketing"
  }'
```

Controlador: [py.edu.uc.jpasseratplp32025.controller.EmpleadoTiempoCompletoController](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

3. Carga en batch de empleados (POST /api/empleados/batch)

```bash
curl -X POST http://localhost:8080/api/empleados/batch \
  -H "Content-Type: application/json" \
  -d '[{ "nombre":"Ana", "apellido":"Gómez", "fechaDeNacimiento":"1990-05-15", "numeroDeCedula":"1111111", "salarioMensual":3000000, "departamento":"Ventas" }, { "nombre":"Carlos", "apellido":"Ramírez", "fechaDeNacimiento":"1985-10-20", "numeroDeCedula":"2222222", "salarioMensual":4500000, "departamento":"IT" }]'
```

- El servicio valida duplicados en el mismo lote y existencia previa en BD.
- Servicio: [py.edu.uc.jpasseratplp32025.service.EmpleadoTiempoCompletoService](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

4. Consultar impuestos de un empleado (GET /api/empleados/{id}/impuestos)

```bash
curl -X GET http://localhost:8080/api/empleados/1/impuestos
```

- Devuelve DTO: [py.edu.uc.jpasseratplp32025.dto.EmpleadoTiempoCompletoImpuestoDto](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

5. Listado polimórfico de remuneraciones (GET /api/remuneraciones/todos)

```bash
curl -X GET http://localhost:8080/api/remuneraciones/todos
```

- Servicio que mapea todas las filas de [personas](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) a [EmpleadoDto](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) usando [obtenerInformacionCompleta()](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) polimórfico.
- Servicio: [py.edu.uc.jpasseratplp32025.service.RemuneracionesService](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

## Manejo de errores

- Errores de validación y parsing JSON devuelven [ErrorResponseDto](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) (JSON) con campos: [timestamp](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [status](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [message](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [path](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [error](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html).
    - Global handler: [py.edu.uc.jpasseratplp32025.exception.GlobalExceptionHandler](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
    - DTO: [ErrorResponseDto.java](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

Ejemplo de error por fecha futura (HTTP 400):

```bash
{
  "timestamp":"2025-10-17T14:33:09.511",
  "status":400,
  "message":"La fecha de nacimiento no puede ser en el futuro",
  "path":"uri=/api/personas",
  "error":"Validation Error"
}
```

Ejemplo operación batch con datos inválidos (HTTP 400):
```bash
[
	{
		"nombre": "Falla",
		"apellido": "Cedula",
		"fechaDeNacimiento": "1985-01-01",
		"numeroDeCedula": "01",
		"salarioMensual": 3000000.00,
		"departamento": "Contabilidad"
	},
	{
		"nombre": "Juan",
		"apellido": "Valido",
		"fechaDeNacimiento": "1999-12-31",
		"numeroDeCedula": "123456",
		"salarioMensual": 4000000.00,
		"departamento": "Marketing"
	}
]
```
Respuesta:
```bash
Error en la carga masiva: Fallo de Bean Validation: numeroDeCedula: El número de cédula debe ser un valor numérico positivo (mayor a 0) de entre 1 y 20 dígitos.
```

## Utilidades

- H2 Console: [http://localhost:8080/h2-console](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html) (configurada en [application.properties](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html))
- POM / dependencias: [pom.xml](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)

---

Archivos relevantes:

- Aplicación: [Jpasseratplp32025Application.java](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
- Entidad base: [PersonaJpa.java](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
- Controladores: [PersonaController.java](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [EmpleadoTiempoCompletoController.java](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
- Servicios: [PersonaService.java](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html), [EmpleadoTiempoCompletoService.java](vscode-file://vscode-app/c:/Users/espin/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-browser/workbench/workbench.html)
