package py.edu.uc.jpasseratplp32025.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("CONTRATISTA")
public class Contratista extends PersonaJpa {

    // nullable = true porque usamos SINGLE_TABLE: otras subclases (ej. EMPLEADO) no proporcionarán estos valores
    @Column(name = "monto_por_proyecto", nullable = true, precision = 12, scale = 2)
    private BigDecimal montoPorProyecto;

    @Column(name = "proyectos_completados", nullable = true)
    private Integer proyectosCompletados;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    // nullable = true porque usamos SINGLE_TABLE y otras subclases no tendrán este valor
    @Column(name = "fecha_fin_contrato", nullable = true)
    private LocalDate fechaFinContrato;

    // Constructores
    public Contratista() { super(); }
    public Contratista(String nombre, String apellido, LocalDate fechaDeNacimiento, String numeroDeCedula,
                       BigDecimal montoPorProyecto, Integer proyectosCompletados, LocalDate fechaFinContrato) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula);
        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;
        this.fechaFinContrato = fechaFinContrato;
    }

    //
    // Implementación de Métodos Abstractos de PersonaJpa
    //

    @Override
    public BigDecimal calcularSalario() {
        if (this.proyectosCompletados == null || this.montoPorProyecto == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal proyectosBD = new BigDecimal(this.proyectosCompletados);
        return this.montoPorProyecto.multiply(proyectosBD).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDeducciones() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); // Sin deducciones
    }

    @Override
    public boolean validarDatosEspecificos() {
        // Valida que el monto por proyecto sea positivo, proyectos >= 0, y fecha futura.
        if (this.montoPorProyecto == null || this.montoPorProyecto.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (this.proyectosCompletados == null || this.proyectosCompletados < 0) {
            return false;
        }
        if (this.fechaFinContrato == null || !this.fechaFinContrato.isAfter(LocalDate.now())) {
            return false;
        }
        return true;
    }

    //
    // Comportamiento Adicional y Getters/Setters
    //

    public boolean contratoVigente() {
        return this.fechaFinContrato != null && this.fechaFinContrato.isAfter(LocalDate.now());
    }

    @Override
    public String obtenerInformacionCompleta() {
        String datos = super.obtenerInformacionCompleta();
        if (!validarDatosEspecificos()) {
            return datos + ", ERROR: Datos de Contratista Inválidos.";
        }
        return datos + String.format(", Tipo: Contratista, Monto Proyecto: %s, Proyectos Completados: %d, Fin Contrato: %s, Vigente: %b, Impuestos: %s",
                this.montoPorProyecto.toString(), this.proyectosCompletados, this.fechaFinContrato.toString(), this.contratoVigente(), this.calcularImpuestos().toString());
    }

    public BigDecimal getMontoPorProyecto() { return montoPorProyecto; }
    public void setMontoPorProyecto(BigDecimal montoPorProyecto) { this.montoPorProyecto = montoPorProyecto; }
    public Integer getProyectosCompletados() { return proyectosCompletados; }
    public void setProyectosCompletados(Integer proyectosCompletados) { this.proyectosCompletados = proyectosCompletados; }
    public LocalDate getFechaFinContrato() { return fechaFinContrato; }
    public void setFechaFinContrato(LocalDate fechaFinContrato) { this.fechaFinContrato = fechaFinContrato; }
}