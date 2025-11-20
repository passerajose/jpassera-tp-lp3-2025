# jpasseratplp32025

Proyecto Spring Boot para gestión de personas y empleados (herencia JPA, validaciones, gestión de permisos y endpoints REST).

## Requisitos

- Java 21
- Maven (o usar `./mvnw` / `mvnw.cmd`)
- Puerto por defecto: **8080**

---

## Ejecutar la aplicación

1. Compilar y ejecutar con Maven wrapper:
    
```bash
./mvnw spring-boot:run
```

o con Maven instalado:

```bash
mvn spring-boot:run
```

2. Clase principal: `py.edu.uc.jpasseratplp32025.Jpasseratplp32025Application`
3. Configuración de BD/H2: `application.properties`

---

## Arquitectura del Proyecto (Spring + JPA Inheritance)

La arquitectura sigue el patrón MVC y se extiende para manejar complejidad de negocio y estructurar el código con jerarquías.

### Jerarquía de Entidades (JPA Single Table)

- **Entidad base:** `py.edu.uc.jpasseratplp32025.entity.PersonaJpa`
    
    - **Empleado Base (Abstracto):** `py.edu.uc.jpasseratplp32025.entity.Empleado` (Implementa `Permisionable`)
        
        - `py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto`
            
        - `py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora`
            
        - `py.edu.uc.jpasseratplp32025.entity.Contratista`
            
        - **Gerente (Rol Especial):** `py.edu.uc.jpasseratplp32025.entity.Gerente` (Implementa `AprobadoGerencial`)
            
- **Interfaces de Negocio:** `py.edu.uc.jpasseratplp32025.interfaces.Permisionable`, `py.edu.uc.jpasseratplp32025.interfaces.AprobadoGerencial`, `py.edu.uc.jpasseratplp32025.interfaces.Mapeable`
    
### Descripción de las Capas del Proyecto

| **Capa/Paquete** | **Clases relevantes**                                                                                                                                                                                                                                | **Descripción**                                                                                                                                                                                                                                                    |
| ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`controller`** | `BaseEmpleadoController`, `GerenteController`,<br>`ContratistaController`,<br>`PersonaController`,<br>`EmpleadoPorHorasController`,<br>`EmpleadoTiempoCompletoController`,<br>`NominaController`,<br>`RemuneracController`,<br>`IndexController`<br> | Jerarquía REST para manejar operaciones CRUD y la solicitud de permisos (`procesarSolicitudPermiso`).                                                                                                                                                              |
| `dto`            | `BaseDto`, `EmpleadoDto`, `EmpleadoExterno`, `EmpleadoTiempoCompletoDto`, `ErrorResponseDto`, `SaludoDto`, `SolicitudPermisoDto`                                                                                                                     | **Objetos de transferencia usados para comunicar datos entre el cliente y la API.** Evitan exponer entidades internas, permiten validación de entrada, y estructuran la carga útil para diferentes tipos de empleado, solicitudes y respuestas de error.           |
| **`exception`**  | `GlobalExceptionHandler`, `DiasInsuficientesException`, `FechaNacimientoFuturaException`, `EmpleadoNoEncontradoException`, `PermisoNoConcedidoException`                                                                                             | Manejo de errores centralizado, asegurando respuestas JSON (4xx/5xx).                                                                                                                                                                                              |
| **`mapper`**     | `BaseMapper`, `GerenteMapper`, `EmpleadoIntegracionMapper`, `IntegracionMapper`, `EmpleadoPorHoraMapper`, `EmpleadoTiempoCompletoMapper`                                                                                                             | Jerarquía para mapear entidades y DTOs, especialmente en integración de datos (e.g., `EmpleadoExterno` a entidades internas).                                                                                                                                      |
| **`service`**    | `GerenteService`, `EmpleadoTiempoCompletoService`, `EmpleadoPorHoraService`, `ContratistaService`, `NominaService`, `RemuneracionesService`, `PersonaService`                                                                                        | Contiene la lógica de negocio, validaciones complejas (ej. _batch_ de empleados, días de permiso).                                                                                                                                                                 |
| **`util`**       | `NominaUtils`, `MapeableFactory`, `MapeableProcessor`                                                                                                                                                                                                | Utilidades estáticas para cálculos de nómina, días de permiso y generación de reportes.                                                                                                                                                                            |
| `repository`     | `ContratistaRepository`, `EmpleadoPorHorasRepository`, `EmpleadoTiempoCompletoRepository`, `GerenteRepository`, `PersonaRepository`                                                                                                                  | **Interfaces que extienden `JpaRepository` o `CrudRepository`, proporcionando operaciones CRUD automáticas** (como `findAll`, `findById`, `save`, `delete`). Definen consultas especializadas cuando es necesario y actúan como capa de acceso a la base de datos. |
| `model`          | `Avatar`, `PosicionGPS`                                                                                                                                                                                                                              | **Objetos del dominio que representan modelos auxiliares o embebidos**, como datos de ubicación (`PosicionGPS`) o recursos visuales (`Avatar`). Suelen utilizarse dentro de entidades principales o para enriquecer la información del empleado.                   |

---

## Reglas de Validación de Datos (Importante)

La API aplica validaciones de datos y de negocio en todos los _endpoints_ de creación y actualización (`POST`/`PUT`).

|**Atributo**|**Regla de Validación**|**Excepción de Negocio (HTTP Status)**|
|---|---|---|
|**`fechaDeNacimiento`**|**No puede ser posterior a la fecha actual.**|`FechaNacimientoFuturaException` (400 Bad Request)|
|**Días de Permiso**|**Empleados:** Máximo 20 días al año. **Gerentes:** No tienen límite.|`DiasInsuficientesException` (400 Bad Request)|
|**ID/Entidad**|Debe existir al consultar, actualizar o eliminar.|`EmpleadoNoEncontradoException` (404 Not Found)|
|**Campos obligatorios**|Validación estándar de Jakarta Bean Validation.|`MethodArgumentNotValidException` (400 Bad Request)|

### Estructura de Error

Todos los errores de validación, negocio o recursos no encontrados son devueltos como un objeto JSON estructurado:

```json
{
  "timestamp": "YYYY-MM-DDTHH:MM:SS.sss",
  "status": 400,
  "message": "Mensaje específico de la excepción (ej. La fecha de nacimiento no puede ser en el futuro)",
  "path": "uri=/api/empleados",
  "error": "Validation Error" 
}
```

---

## Endpoints Clave y Uso Por Tipo de Empleado

Nota: usar header `Content-Type: application/json`. Fechas en formato ISO `yyyy-MM-dd`.

### 1. Gestión de Gerentes

|**Endpoint**|**Descripción**|
|---|---|
|`POST /api/gerentes`|Crea un nuevo Gerente.|
|`PUT /api/gerentes/{id}`|Actualiza datos de un Gerente.|
|`POST /api/gerentes/{id}/permiso`|Solicita permiso para un Gerente (no aplica límite de días).|
|`GET /api/gerentes/{id}/nomina-departamento`|Calcula la nómina total del departamento a cargo del Gerente.|

**Ejemplo de Creación de Gerente:**

```bash
curl --request POST \
  --url http://localhost:8080/api/gerentes \
  --header 'Content-Type: application/json' \
  --data '{
	"nombre": "Filippi", 
	"apellido": "Menchi", 
	"numeroDeCedula": "6666666",
	"fechaDeNacimiento": "2000-05-15",
	"fechaIngreso": "2030-06-01", 
	"departamentoACargo": "Finanzas",
	"fechaFinContrato": "2031-12-31"
}'
```

### 2. Gestión de Contratistas

|**Endpoint**|**Descripción**|
|---|---|
|`POST /api/contratistas`|Crea un nuevo Contratista (aplica validación de `fechaDeNacimiento`).|
|`GET /api/contratistas`|Lista todos los Contratistas.|
|`GET /api/contratistas/{id}`|Obtiene un Contratista por ID (lanza `EmpleadoNoEncontradoException` si no existe).|
|`PUT /api/contratistas/{id}`|Actualiza un Contratista (aplica validación de `fechaDeNacimiento`).|
|`DELETE /api/contratistas/{id}`|Elimina un Contratista.|
|`POST /api/contratistas/{id}/permiso`|Solicita permiso para el Contratista. **Aplica límite de 20 días/año.**|
|`GET /api/contratistas/vigentes`|Lista los Contratistas con contratos activos.|
|`GET /api/contratistas/nomina-total`|Calcula la nómina total para todos los Contratistas.|

**Ejemplo de Creación de un empleado de tipo Contratista**

```bash
curl --request POST \
  --url http://localhost:8080/api/contratistas \
  --header 'Content-Type: application/json' \
  --data '{
	"nombre": "Piki",
	"apellido": "Gomez",
	"numeroDeCedula": "9999919",
	"fechaDeNacimiento": "1999-05-20",
	"fechaIngreso": "2024-01-01",
	"diasVacacionesAcumulados": 10,
	"montoPorProyecto": 2000000,
	"proyectosCompletados": 5,
	"fechaFinContrato": "2026-06-30"
}'
```

### 3. Gestión de Empleados de Tiempo Completo

|**Endpoint**|**Descripción**|
|---|---|
|`POST /api/empleados`|Crea un nuevo Empleado de Tiempo Completo (aplica validación de `fechaDeNacimiento`).|
|`POST /api/empleados/batch`|Carga masiva de Empleados de Tiempo Completo.|
|`GET /api/empleados`|Lista todos los Empleados.|
|`GET /api/empleados/{id}`|Obtiene un Empleado por ID.|
|`PUT /api/empleados/{id}`|Actualiza un Empleado (aplica validación de `fechaDeNacimiento`).|
|`DELETE /api/empleados/{id}`|Elimina un Empleado.|
|`POST /api/empleados/{id}/permiso`|Solicita permiso para el Empleado. **Aplica límite de 20 días/año.**|
|`GET /api/empleados/{id}/salario-neto`|Calcula el salario neto después de deducciones.|
|`GET /api/empleados/{id}/impuestos`|Devuelve información detallada del impuesto en un DTO.|
|`GET /api/empleados/departamento?nombre=X`|Busca empleados por departamento.|
|`GET /api/empleados/vigentes`|Lista empleados con contratos vigentes.|
|`GET /api/empleados/nomina-total`|Calcula la nómina total para Empleados de Tiempo Completo.|

**Ejemplo de Creación de un empleado de tipo EmpleadoTiempoCompleto**

```bash
curl --request POST \
  --url http://localhost:8080/api/empleados \
  --header 'Content-Type: application/json' \
  --data '{
	"nombre": "Carla",
	"apellido": "Mendez",
	"numeroDeCedula": "555",
	"fechaDeNacimiento": "2000-11-17",
	"salarioMensual": 6000000,
	"departamento": "IT",
	"fechaFinContrato": "2029-01-01"
}'
```

### 4. Gestión de Empleados Por Hora

|**Endpoint**|**Descripción**|
|---|---|
|`POST /api/empleados-por-hora`|Crea un nuevo Empleado por Hora (aplica validación de `fechaDeNacimiento`).|
|`GET /api/empleados-por-hora`|Lista todos los Empleados por Hora.|
|`GET /api/empleados-por-hora/{id}`|Obtiene un Empleado por ID.|
|`PUT /api/empleados-por-hora/{id}`|Actualiza un Empleado (aplica validación de `fechaDeNacimiento`).|
|`DELETE /api/empleados-por-hora/{id}`|Elimina un Empleado.|
|`POST /api/empleados-por-hora/{id}/permiso`|Solicita permiso para el Empleado. **Aplica límite de 20 días/año.**|
|`GET /api/empleados-por-hora/consulta?horas=X`|Lista empleados que hayan trabajado más de un número de horas específico.|
|`GET /api/empleados-por-hora/vigentes`|Lista empleados con contratos vigentes.|
|`GET /api/empleados-por-hora/nomina-total`|Calcula la nómina total para Empleados por Hora.|

**Ejemplo de Consulta por Horas Trabajadas**

```bash
curl -X GET "http://localhost:8080/api/empleados-por-hora/consulta?horas=40"
```
> Para obtener una respuesta con este ejemplo asegurarse de tener al menos un empleado por hora con una cantidad de horas mayor o igual a la especificada, en este caso 40 horas.

### 5. Solicitud de Permisos (Validación de Días)

|**Endpoint**|**Descripción**|
|---|---|
|`POST /api/empleados/{id}/permiso`|Solicita permiso para Empleados de Tiempo Completo.|
|`POST /api/contratistas/{id}/permiso`|Solicita permiso para Contratistas.|
|`POST /api/empleados-por-hora/{id}/permiso`|Solicita permiso para Empleados por Hora.|

**Ejemplo de Solicitud de Permiso (puede lanzar DiasInsuficientesException):**

```bash
curl --request POST \
  --url http://localhost:8080/api/empleados/1/permisos \
  --header 'Content-Type: application/json' \
  --data '{
	"fechaInicio": "2026-04-01",
	"fechaFin": "2026-04-10",
	"tipoPermiso": "VACACIONES"
}'
```
> Para obtener una respuesta con este ejemplo asegurarse de tener creado un empleado de tiempo completo con el id especificado, para este caso se asume que el id es 1.

### 6. Consultas de Nómina / Utilidades

|**Endpoint**|**Descripción**|
|---|---|
|`GET /api/empleados/nomina-total`|Retorna la suma total de salarios brutos para empleados de tiempo completo.|
|`GET /api/contratistas/nomina-total`|Retorna la suma total de salarios brutos para contratistas.|
|`GET /api/empleados-por-hora/nomina-total`|Retorna la suma total de salarios brutos para empleados por hora.|
|`GET /api/remuneraciones/todos`|Listado polimórfico de remuneraciones.|

---

## Utilidades

- H2 Console: `http://localhost:8080/h2-console`
- Archivos relevantes:
	- `py.edu.uc.jpasseratplp32025.exception.GlobalExceptionHandler.java`
	- `py.edu.uc.jpasseratplp32025.util.NominaUtils.java`
	- `py.edu.uc.jpasseratplp32025.controller.BaseEmpleadoController.java`
