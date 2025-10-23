package py.edu.uc.jpasseratplp32025.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import java.time.LocalDate;
import java.math.BigDecimal; // Necesario para calcularSalario y calcularImpuestos
import java.math.RoundingMode;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //  crea una única tabla (llamada personas) que contiene columnas para todos los campos de la clase base y todos los campos específicos de sus subclases (los que heredan de PersonaJpa)
@DiscriminatorColumn(name = "tipo_persona", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("PERSONA")
public abstract class PersonaJpa { // <<-- CLASE ES ABSTRACTA

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaDeNacimiento;

    @NotBlank(message = "El número de cédula no puede estar vacío.")
    @Pattern(regexp = "^[1-9]\\d{0,19}$",
            message = "El número de cédula debe ser un valor numérico positivo (mayor a 0) de entre 1 y 20 dígitos.")
    @Column(name = "numero_cedula", nullable = false, unique = true, length = 20)
    private String numeroDeCedula;

    // =================================================================
    // MÉTODOS DE NEGOCIO Y TEMPLATE
    // =================================================================

    /**
     * MÉTODO ABSTRACTO: Fuerza a las subclases a definir cómo se calcula el salario.
     */
    public abstract BigDecimal calcularSalario();

    /**
     * MÉTODO ABSTRACTO: Fuerza a las subclases a definir sus deducciones específicas.
     */
    public abstract BigDecimal calcularDeducciones();

    /**
     * MÉTODO TEMPLATE: Calcula el impuesto total usando el Salario y las Deducciones.
     * Patrón Template Method: Define el esqueleto de un algoritmo (cálculo de impuestos),
     * dejando que las subclases definan ciertos pasos (calcularDeducciones).
     */
    public final BigDecimal calcularImpuestos() {
        BigDecimal salarioBruto = this.calcularSalario();
        BigDecimal deducciones = this.calcularDeducciones();

        // Salario Imponible = Salario Bruto - Deducciones
        BigDecimal salarioImponible = salarioBruto.subtract(deducciones);
        if (salarioImponible.compareTo(BigDecimal.ZERO) < 0) {
            salarioImponible = BigDecimal.ZERO;
        }

        // Impuesto Total = Salario Imponible - Impuesto Base (10%)
        return salarioImponible.subtract(this.calcularImpuestoBase()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * MÉTODO CONCRETO: Calcula el impuesto base (10% del salario).
     */
    private BigDecimal calcularImpuestoBase() {
        final BigDecimal TASA_BASE = new BigDecimal("0.10"); // 10%
        return this.calcularSalario().multiply(TASA_BASE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * MÉTODO CONCRETO (sobreescribible): Proporciona la información base.
     */
    public String obtenerInformacionCompleta() {
        return String.format("ID: %d, Nombre: %s, Apellido: %s, Fecha Nacimiento: %s, Cédula: %s",
                this.id, this.nombre, this.apellido, this.fechaDeNacimiento.toString(), this.numeroDeCedula);
    }

    /**
     * MÉTODO ABSTRACTO: Fuerza a las subclases a implementar su lógica de validación específica.
     * Retorna boolean: true si los datos son válidos, false si no lo son.
     */
    public abstract boolean validarDatosEspecificos();

    // =================================================================
    // CONSTRUCTORES, GETTERS y SETTERS (Sin cambios)
    // =================================================================

    public PersonaJpa() {
    }

    public PersonaJpa(String nombre, String apellido, LocalDate fechaDeNacimiento, String numeroDeCedula) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.numeroDeCedula = numeroDeCedula;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public LocalDate getFechaDeNacimiento() { return fechaDeNacimiento; }
    public void setFechaDeNacimiento(LocalDate fechaDeNacimiento) { this.fechaDeNacimiento = fechaDeNacimiento; }
    public String getNumeroDeCedula() { return numeroDeCedula; }
    public void setNumeroDeCedula(String numeroDeCedula) { this.numeroDeCedula = numeroDeCedula; }
}