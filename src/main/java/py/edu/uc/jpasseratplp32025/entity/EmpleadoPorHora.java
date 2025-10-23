package py.edu.uc.jpasseratplp32025.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("HORA")
public class EmpleadoPorHora extends PersonaJpa {

    private static final int HORAS_NORMALES = 40;
    private static final BigDecimal PORCENTAJE_DEDUCCION = new BigDecimal("0.02");
    private static final BigDecimal BONUS_HORA_EXTRA = new BigDecimal("1.50");

    // Permitir NULL porque usamos SINGLE_TABLE y otras subclases no proporcionarán estos valores
    @Column(name = "tarifa_por_hora", nullable = true, precision = 10, scale = 2)
    private BigDecimal tarifaPorHora;

    @Column(name = "horas_trabajadas", nullable = true)
    private Integer horasTrabajadas;

    // Constructores
    public EmpleadoPorHora() { super(); }
    public EmpleadoPorHora(String nombre, String apellido, LocalDate fechaDeNacimiento, String numeroDeCedula,
                           BigDecimal tarifaPorHora, Integer horasTrabajadas) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
    }

    //
    // Implementación de Métodos Abstractos de PersonaJpa
    //

    @Override
    public BigDecimal calcularSalario() {
        if (this.horasTrabajadas == null || this.horasTrabajadas <= 0 || this.tarifaPorHora == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal horasNormalesBD = new BigDecimal(Math.min(this.horasTrabajadas, HORAS_NORMALES));
        BigDecimal salarioBase = this.tarifaPorHora.multiply(horasNormalesBD);

        if (this.horasTrabajadas > HORAS_NORMALES) {
            BigDecimal horasExtra = new BigDecimal(this.horasTrabajadas - HORAS_NORMALES);
            BigDecimal bonus = this.tarifaPorHora.multiply(BONUS_HORA_EXTRA).multiply(horasExtra);
            salarioBase = salarioBase.add(bonus);
        }
        return salarioBase.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDeducciones() {
        return this.calcularSalario().multiply(PORCENTAJE_DEDUCCION).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean validarDatosEspecificos() {
        // Valida que la tarifa sea positiva y las horas estén en el rango [1, 80].
        if (this.tarifaPorHora == null || this.tarifaPorHora.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (this.horasTrabajadas == null || this.horasTrabajadas < 1 || this.horasTrabajadas > 80) {
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
        if (!validarDatosEspecificos()) {
            return datos + ", ERROR: Datos de Empleado Inválidos.";
        } 
        return datos + String.format(", Tipo: Por Hora, Tarifa: %s, Horas Trabajadas: %d, Salario Total: %s, Impuestos: %s",
                this.tarifaPorHora.toString(), this.horasTrabajadas, this.calcularSalario().toString(), this.calcularImpuestos().toString());
    }

    public BigDecimal getTarifaPorHora() { return tarifaPorHora; }
    public void setTarifaPorHora(BigDecimal tarifaPorHora) { this.tarifaPorHora = tarifaPorHora; }
    public Integer getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Integer horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
}