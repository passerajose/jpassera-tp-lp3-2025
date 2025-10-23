package py.edu.uc.jpasseratplp32025.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("EMPLEADO")
public class EmpleadoTiempoCompleto extends PersonaJpa {

    // Constantes para el cálculo
    private static final BigDecimal DESCUENTO_CALCULO_SALARIO = new BigDecimal("0.09"); // 9% para calcular salario
    private static final BigDecimal DEDUCCION_FIJA = new BigDecimal("0.05"); // 5% para calcular deducciones
    private static final BigDecimal SALARIO_MINIMO_REQUERIDO = new BigDecimal("2899048"); // 2,899,048

    @Column(name = "salario_mensual", nullable = true, precision = 10, scale = 2)
    private BigDecimal salarioMensual;

    // CORRECCIÓN CLAVE: Cambiar nullable = false a nullable = true
    // Esto es necesario en SINGLE_TABLE porque Contratista y EmpleadoPorHora
    // no usan esta columna, por lo que será NULL para ellos.
    @Column(nullable = true, length = 50)
    private String departamento;

    public EmpleadoTiempoCompleto() {
        super();
    }

    public EmpleadoTiempoCompleto(String nombre, String apellido, LocalDate fechaDeNacimiento, String numeroDeCedula,
                                  BigDecimal salarioMensual, String departamento) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula);
        this.salarioMensual = salarioMensual;
        this.departamento = departamento;
    }

    //
    // Implementación de Métodos Abstractos de PersonaJpa
    //

    /**
     * Calcula el salario aplicando un descuento del 9% (Salario Neto).
     */
    @Override
    public BigDecimal calcularSalario() {
        if (this.salarioMensual == null) {
            return BigDecimal.ZERO;
        }
        // Salario = Salario Mensual - (Salario Mensual * 9%)
        BigDecimal descuento = this.salarioMensual.multiply(DESCUENTO_CALCULO_SALARIO);
        return this.salarioMensual.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Aplica una deducción fija del 5% del salario mensual.
     */
    @Override
    public BigDecimal calcularDeducciones() {
        if (this.salarioMensual == null || this.salarioMensual.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // Deducción = Salario Mensual * 5%
        return this.salarioMensual.multiply(DEDUCCION_FIJA).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Valida que el salario sea >= 2899048 y el departamento no esté vacío.
     */
    // **IMPORTANTE:** Aunque permitas NULL en la BD, debes mantener tu lógica de negocio
    // en validarDatosEspecificos() para asegurar que el EmpleadoTiempoCompleto tenga un departamento.
    @Override
    public boolean validarDatosEspecificos() {
        // 1. Validar que el salario sea mayor o igual al mínimo requerido
        if (this.salarioMensual == null || this.salarioMensual.compareTo(SALARIO_MINIMO_REQUERIDO) < 0) {
             return false;
        }
        // 2. Validar que el departamento no esté vacío (ESTO MANTIENE LA REGLA DE NEGOCIO)
        if (this.departamento == null || this.departamento.trim().isEmpty()) {
             return false;
        }
        return true;
    }

    //
    // Comportamiento Adicional y Getters/Setters
    //

    @Override
    public String obtenerInformacionCompleta() {
        String datos = super.obtenerInformacionCompleta();

        String departamentoStr = (this.departamento == null || this.departamento.trim().isEmpty()) ? "N/A" : this.departamento;
        String salarioBrutoStr = (this.salarioMensual == null) ? "N/A"
                : this.salarioMensual.setScale(2, RoundingMode.HALF_UP).toString();

        BigDecimal salarioNeto = this.calcularSalario();
        String salarioNetoStr = (salarioNeto == null) ? "N/A"
                : salarioNeto.setScale(2, RoundingMode.HALF_UP).toString();

        BigDecimal deducciones = this.calcularDeducciones();
        String deduccionesStr = (deducciones == null) ? "N/A"
                : deducciones.setScale(2, RoundingMode.HALF_UP).toString();

        BigDecimal impuestos = null;
        try {
            impuestos = this.calcularImpuestos();
        } catch (Exception e) {
            impuestos = null;
        }
        String impuestosStr = (impuestos == null) ? "N/A" : impuestos.setScale(2, RoundingMode.HALF_UP).toString();

        if (!validarDatosEspecificos()) {
            return String.format("%s, ERROR: Datos de Empleado Inválidos (Salario mínimo o Departamento inválido). Departamento: %s, Salario Mensual Bruto: %s",
                    datos, departamentoStr, salarioBrutoStr);
        }

        return String.format("%s, Departamento: %s, Salario Mensual Bruto: %s, Salario Neto (9%%): %s, Deducciones (5%%): %s, Impuestos: %s",
                datos, departamentoStr, salarioBrutoStr, salarioNetoStr, deduccionesStr, impuestosStr);
    }

    public BigDecimal getSalarioMensual() { return salarioMensual; }
    public void setSalarioMensual(BigDecimal salarioMensual) { this.salarioMensual = salarioMensual; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}