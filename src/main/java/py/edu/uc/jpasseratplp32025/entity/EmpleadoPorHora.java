package py.edu.uc.jpasseratplp32025.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("HORA")
public class EmpleadoPorHora extends Empleado {

    private static final int HORAS_NORMALES = 40;
    private static final BigDecimal PORCENTAJE_DEDUCCION = new BigDecimal("0.02");
    private static final BigDecimal BONUS_HORA_EXTRA = new BigDecimal("1.50");

    // Mantener este campo, pero su actualización la haremos en los setters y el servicio.
    @Column(name = "salario_mensual", nullable = true, precision = 10, scale = 2)
    private BigDecimal salarioMensual;

    @Column(name = "tarifa_por_hora", nullable = true, precision = 10, scale = 2)
    private BigDecimal tarifaPorHora;

    @Column(name = "horas_trabajadas", nullable = true)
    private Integer horasTrabajadas;

    //
    // Constructores
    //
    public EmpleadoPorHora() {
        super();
        this.salarioMensual = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public EmpleadoPorHora(String nombre, String apellido, LocalDate fechaDeNacimiento,
                           String numeroDeCedula, BigDecimal tarifaPorHora, Integer horasTrabajadas,
                           LocalDate fechaFinContrato) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula,
                LocalDate.now(), // fechaIngreso
                10, // diasVacacionesIniciales por defecto
                fechaFinContrato);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
        this.salarioMensual = calcularSalario(); // Se inicializa con el cálculo
    }

    //
    // Implementación de Métodos Abstractos de PersonaJpa
    //

    @Override
    public BigDecimal calcularSalario() {
        if (this.horasTrabajadas == null || this.horasTrabajadas <= 0 || this.tarifaPorHora == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
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

    public BigDecimal getSalarioMensual() {
        // Si por alguna razón es nulo o cero, forzamos el cálculo antes de devolver.
        if (salarioMensual == null || salarioMensual.compareTo(BigDecimal.ZERO) == 0) {
            return calcularSalario();
        }
        return salarioMensual;
    }
    public void setSalarioMensual(BigDecimal salarioMensual) { this.salarioMensual = salarioMensual; }

    public BigDecimal getTarifaPorHora() { return tarifaPorHora; }
    public void setTarifaPorHora(BigDecimal tarifaPorHora) {
        this.tarifaPorHora = tarifaPorHora;
        this.salarioMensual = calcularSalario(); // <-- Recálculo forzado
    }
    public Integer getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Integer horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
        this.salarioMensual = calcularSalario(); // <-- Recálculo forzado
    }
}