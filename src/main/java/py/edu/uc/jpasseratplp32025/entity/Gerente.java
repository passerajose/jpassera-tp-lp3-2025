package py.edu.uc.jpasseratplp32025.entity;

import py.edu.uc.jpasseratplp32025.interfaces.AprobadorGerencial;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException; // <-- Import necesario

import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@DiscriminatorValue("GERENTE")
@Slf4j
public class Gerente extends Empleado implements AprobadorGerencial {

    // Atributos específicos del Gerente
    private String departamentoACargo;

    // Constructores...
    public Gerente() {
        super();
        this.departamentoACargo = "General";
    }

    public Gerente(String nombre, String apellido, LocalDate fechaDeNacimiento,
                   String numeroDeCedula, LocalDate fechaIngreso,
                   String departamentoACargo, LocalDate fechaFinContrato) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula,
                fechaIngreso,
                30, // Días de vacaciones iniciales más altos
                fechaFinContrato);
        this.departamentoACargo = departamentoACargo;
        log.info("Gerente de {} creado. Permisos extendidos aplicados.", departamentoACargo);
    }


    // =================================================================
    // MÉTODOS PERMISIONABLE (POLIMORFISMO: SOBREESCRITURA)
    // =================================================================

    /**
     * SOBREESCRITURA: El Gerente puede acumular más de 20 días al año.
     * La implementación ignora el control del límite anual, manteniendo solo
     * el control de antigüedad y saldo disponible.
     */
    @Override
    public boolean solicitarPermiso(LocalDate fechaInicio,
                                    LocalDate fechaFin,
                                    String tipoPermiso,
                                    String codigoEmpleado)
            throws PermisoNoConcedidoException, DiasInsuficientesException { // <-- Firma Actualizada

        long diasSolicitados = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        int diasAcumulados = this.consultarDiasVacacionesDisponibles(codigoEmpleado);

        if (diasSolicitados <= 0) {
            throw new PermisoNoConcedidoException(
                    "La solicitud de permiso debe ser por al menos un día.",
                    "Días solicitados inválidos"
            );
        }

        if (tipoPermiso.equalsIgnoreCase("VACACIONES")) {
            // 1. Antigüedad (Se usa la lógica heredada)
            if (!tieneAntiguedadParaVacaciones(codigoEmpleado)) {
                throw new PermisoNoConcedidoException(
                        "El empleado no cumple con la antigüedad mínima de 1 año para vacaciones.",
                        "Antigüedad insuficiente (Mínimo 1 año)"
                );
            }

            // 2. Saldo de Días
            if (diasSolicitados > diasAcumulados) {
                throw new PermisoNoConcedidoException(
                        String.format("Días solicitados (%d) exceden el saldo disponible (%d).", diasSolicitados, diasAcumulados),
                        "Insuficiencia de días disponibles (Gerente)"
                );
            }

            // 3. Simulación de Aprobación
            this.setDiasVacacionesAcumulados(diasAcumulados - (int) diasSolicitados);
            this.diasVacacionesSolicitadosTotal += (int) diasSolicitados; // Actualiza el contador anual (puede exceder 20)

            log.info("✅ Solicitud de vacaciones de {} días aprobada provisionalmente (Gerente). Saldo restante: {}", diasSolicitados, this.consultarDiasVacacionesDisponibles(codigoEmpleado));
            return true;
        }

        // Para otros permisos (MATRIMONIO, etc.), se utiliza la lógica heredada.
        return super.solicitarPermiso(fechaInicio, fechaFin, tipoPermiso, codigoEmpleado);
    }

    // =================================================================
    // OTROS MÉTODOS
    // =================================================================

    @Override
    public boolean procesarAprobacionGerencial(long idSolicitud, boolean esAprobado, String notas)
            throws PermisoNoConcedidoException {

        if (!esAprobado) {
            String motivo = notas != null && !notas.trim().isEmpty() ? notas : "Rechazo de Gerencia: Prioridad Operacional.";
            throw new PermisoNoConcedidoException(
                    String.format("La solicitud #%d fue rechazada por Gerente %s.", idSolicitud, this.getNombre()),
                    motivo
            );
        }

        log.info("⭐ Solicitud #{} APROBADA FINALMENTE por el Gerente: {}. Nota: {}", idSolicitud, this.getNombre(), notas);
        return true;
    }

    @Override
    public BigDecimal calcularSalario() {
        // Ejemplo: Gerentes ganan 50% más que el empleado base
        BigDecimal salarioBase = new BigDecimal("5000000.00");
        return salarioBase.multiply(new BigDecimal("1.5")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDeducciones() {
        // Ejemplo: Gerentes tienen 5% más de deducciones
        BigDecimal salario = this.calcularSalario();
        return salario.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String obtenerInformacionCompleta() {
        return super.obtenerInformacionCompleta() + String.format(", Rol: GERENTE (%s)", this.departamentoACargo);
    }

    public String getDepartamentoACargo() { return departamentoACargo; }
    public void setDepartamentoACargo(String departamentoACargo) { this.departamentoACargo = departamentoACargo; }
}