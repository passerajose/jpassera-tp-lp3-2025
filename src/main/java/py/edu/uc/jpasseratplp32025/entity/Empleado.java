package py.edu.uc.jpasseratplp32025.entity;

import py.edu.uc.jpasseratplp32025.interfaces.Mapeable;
import py.edu.uc.jpasseratplp32025.interfaces.Permisionable;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException; // <-- Import de la excepción
import py.edu.uc.jpasseratplp32025.model.Avatar;
import py.edu.uc.jpasseratplp32025.model.PosicionGPS;

import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonFormat;

@Slf4j
@Entity
@DiscriminatorValue("EMP_REGULAR") // EMPLEADO
public class Empleado extends PersonaJpa implements Mapeable, Permisionable {

    // Campos de RR.HH. necesarios para Permisionable
    private LocalDate fechaIngreso;
    private int diasVacacionesAcumulados;
    // Campo que rastrea el total de días solicitados en el año (protected para que Gerente acceda)
    protected int diasVacacionesSolicitadosTotal;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "fecha_fin_contrato", nullable = true)
    private LocalDate fechaFinContrato;

    // Constructores...
    public Empleado() {
        super("Empleado", "MOCK", java.time.LocalDate.of(1990, 1, 1), "9999999");
        this.fechaIngreso = LocalDate.of(2020, 1, 1);
        this.diasVacacionesAcumulados = 30;
        this.diasVacacionesSolicitadosTotal = 0; // Inicialización
    }

    public Empleado(String nombre, String apellido, LocalDate fechaDeNacimiento, String numeroDeCedula, LocalDate fechaIngreso, int diasVacacionesIniciales, LocalDate fechaFinContrato) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula);
        this.fechaIngreso = fechaIngreso;
        this.diasVacacionesAcumulados = diasVacacionesIniciales;
        this.fechaFinContrato = fechaFinContrato;
        this.diasVacacionesSolicitadosTotal = 0; // Inicialización
        log.info("Empleado {} creado con {} días de vacaciones.", nombre, diasVacacionesIniciales);
    }

    public Empleado(String nombre, String apellido, LocalDate fechaDeNacimiento, String numeroDeCedula) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula);
        this.fechaIngreso = LocalDate.now();
        this.diasVacacionesAcumulados = 10;
        this.diasVacacionesSolicitadosTotal = 0; // Inicialización
    }

    // =================================================================
    // MÉTODOS PERMISIONABLE (IMPLEMENTADOS)
    // =================================================================

    @Override
    public boolean solicitarPermiso(LocalDate fechaInicio,
                                    LocalDate fechaFin,
                                    String tipoPermiso,
                                    String codigoEmpleado)
            throws PermisoNoConcedidoException, DiasInsuficientesException { // <-- Firma Actualizada

        long diasSolicitados = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;

        if (diasSolicitados <= 0) {
            throw new PermisoNoConcedidoException(
                    "La solicitud de permiso debe ser por al menos un día.",
                    "Días solicitados inválidos"
            );
        }

        if (tipoPermiso.equalsIgnoreCase("VACACIONES")) {
            // 1. Antigüedad
            if (!tieneAntiguedadParaVacaciones(codigoEmpleado)) {
                throw new PermisoNoConcedidoException(
                        "El empleado no cumple con la antigüedad mínima de 1 año para vacaciones.",
                        "Antigüedad insuficiente (Mínimo 1 año)"
                );
            }

            // 2. Saldo de Días
            if (diasSolicitados > diasVacacionesAcumulados) {
                throw new PermisoNoConcedidoException(
                        String.format("Días solicitados (%d) exceden el saldo disponible (%d).", diasSolicitados, diasVacacionesAcumulados),
                        "Insuficiencia de días disponibles"
                );
            }

            // 3. RESTRICCIÓN DE 20 DÍAS ANUALES (EXCLUSIVA DE EMPLEADOS REGULARES)
            int nuevoTotalSolicitado = this.diasVacacionesSolicitadosTotal + (int) diasSolicitados;
            if (nuevoTotalSolicitado > 20) {
                // Si la solicitud excede el límite acumulado de 20 días en el año, lanzamos la excepción
                throw new DiasInsuficientesException(
                        String.format("Los empleados regulares solo pueden solicitar un total de 20 días de vacaciones al año. Su solicitud excede el límite (Total acumulado: %d).", nuevoTotalSolicitado)
                );
            }

            // 4. Simulación de Aprobación
            diasVacacionesAcumulados -= diasSolicitados;
            this.diasVacacionesSolicitadosTotal = nuevoTotalSolicitado; // Actualizamos el total anual

            log.info("✅ Solicitud de vacaciones de {} días aprobada provisionalmente. Saldo restante: {}", diasSolicitados, diasVacacionesAcumulados);
            return true;

        } else if (tipoPermiso.equalsIgnoreCase("MATRIMONIO") && diasSolicitados <= 4) {
            // Leyes paraguayas: 4 días por matrimonio o nacimiento.
            log.info("✅ Solicitud de permiso especial por {} aprobada automáticamente.", tipoPermiso);
            return true;
        }

        throw new PermisoNoConcedidoException(
                String.format("Solicitud de permiso tipo '%s' denegada. Solo 'VACACIONES' y 'MATRIMONIO' son válidos para demo.", tipoPermiso),
                "Tipo de permiso no válido o sin días especiales asignados"
        );
    }

    @Override
    public int consultarDiasVacacionesDisponibles(String codigoEmpleado) {
        return this.diasVacacionesAcumulados;
    }

    /**
     * Implementación requerida por NominaUtils para calcular el total de días solicitados.
     */
    public int consultarDiasVacacionesSolicitados() {
        return this.diasVacacionesSolicitadosTotal;
    }

    @Override
    public boolean tieneAntiguedadParaVacaciones(String codigoEmpleado) {
        long antiguedadAnios = ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now());
        return antiguedadAnios >= 1;
    }

    // =================================================================
    // OTROS MÉTODOS HEREDADOS
    // =================================================================

    @Override
    public BigDecimal calcularSalario() {
        return new BigDecimal("5000000.00").setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDeducciones() {
        BigDecimal salario = this.calcularSalario();
        return salario.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean validarDatosEspecificos() {
        return true;
    }

    @Override
    public String obtenerInformacionCompleta() {
        return super.obtenerInformacionCompleta() + String.format(", Rol: EMPLEADO");
    }

    @Override
    public PosicionGPS ubicarElemento() {
        final double LAT = -25.298818;
        final double LON = -57.568461;
        return new PosicionGPS(LAT, LON);
    }

    @Override
    public Avatar obtenerImagen() {
        Object imagenMock = new Object();
        String nombreActual = this.getNombre();
        String nombreParaNick = (nombreActual != null && !nombreActual.trim().isEmpty())
                ? nombreActual.toUpperCase()
                : "SIN_NOMBRE";
        String nickMock = "MOCK-" + nombreParaNick;

        return new Avatar(imagenMock, nickMock);
    }

    // Setter necesario para Gerente
    protected void setDiasVacacionesAcumulados(int dias) {
        this.diasVacacionesAcumulados = dias;
    }

    public LocalDate getFechaFinContrato() { return fechaFinContrato; }
    public void setFechaFinContrato(LocalDate fechaFinContrato) { this.fechaFinContrato = fechaFinContrato; }
}