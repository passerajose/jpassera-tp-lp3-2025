package py.edu.uc.jpasseratplp32025.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import py.edu.uc.jpasseratplp32025.model.Avatar;
import py.edu.uc.jpasseratplp32025.model.PosicionGPS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("CONTRATISTA")
public class Contratista extends Empleado {

    @Column(name = "monto_por_proyecto", nullable = true, precision = 12, scale = 2)
    private BigDecimal montoPorProyecto;

    @Column(name = "proyectos_completados", nullable = true)
    private Integer proyectosCompletados;

    // Campo persistente que almacena el resultado del cálculo
    @Column(name = "salario_mensual_calc", nullable = true, precision = 10, scale = 2)
    private BigDecimal salarioMensualPersistido;

    //
    // Constructores
    //
    public Contratista() {
        super();
        this.salarioMensualPersistido = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public Contratista(String nombre, String apellido, LocalDate fechaDeNacimiento,
                       String numeroDeCedula, BigDecimal montoPorProyecto,
                       Integer proyectosCompletados, LocalDate fechaFinContrato) {
        super(nombre, apellido, fechaDeNacimiento, numeroDeCedula,
                LocalDate.now(), // fechaIngreso
                5, // diasVacacionesIniciales por defecto
                fechaFinContrato);

        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;

        // Inicializa el campo persistido llamando al método de cálculo
        this.salarioMensualPersistido = calcularSalario();
    }

    //
    // Implementación de Métodos Abstractos de PersonaJpa
    //

    @Override
    public BigDecimal calcularSalario() {
        if (this.proyectosCompletados == null || this.montoPorProyecto == null) {
            // Devuelve cero si faltan datos para evitar NullPointerException
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
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
        if (this.getFechaFinContrato() == null || !this.getFechaFinContrato().isAfter(LocalDate.now())) {
            return false;
        }
        return true;
    }

    //
    // Comportamiento Adicional y Getters/Setters Corregidos
    //

    public boolean contratoVigente() {
        return this.getFechaFinContrato() != null && this.getFechaFinContrato().isAfter(LocalDate.now());
    }

    @Override
    public String obtenerInformacionCompleta() {
        String datos = super.obtenerInformacionCompleta();
        if (!validarDatosEspecificos()) {
            return datos + ", ERROR: Datos de Contratista Inválidos.";
        }
        return datos + String.format(", Tipo: Contratista, Monto Proyecto: %s, Proyectos Completados: %d, Fin Contrato: %s, Vigente: %b, Impuestos: %s",
                this.montoPorProyecto.toString(), this.proyectosCompletados, this.getFechaFinContrato().toString(), this.contratoVigente(), this.calcularImpuestos().toString());
    }

    // El resto de los métodos Mapeable... (ubicarElemento, obtenerImagen)
    // ...

    // Getters y Setters que fuerzan el recálculo
    public BigDecimal getSalarioMensual() {
        return salarioMensualPersistido != null ? salarioMensualPersistido : calcularSalario();
    }

    // Este getter fue renombrado para reflejar que es el valor persistido
    public BigDecimal getSalarioMensualPersistido() { return salarioMensualPersistido; }
    // Puedes añadir este setter si el servicio lo necesita para forzar la persistencia
    public void setSalarioMensualPersistido(BigDecimal salario) { this.salarioMensualPersistido = salario; }


    public BigDecimal getMontoPorProyecto() { return montoPorProyecto; }
    public void setMontoPorProyecto(BigDecimal montoPorProyecto) {
        this.montoPorProyecto = montoPorProyecto;
        this.salarioMensualPersistido = calcularSalario(); // <--- Recalcular al cambiar
    }

    public Integer getProyectosCompletados() { return proyectosCompletados; }
    public void setProyectosCompletados(Integer proyectosCompletados) {
        this.proyectosCompletados = proyectosCompletados;
        this.salarioMensualPersistido = calcularSalario(); // <--- Recalcular al cambiar
    }

    // Resto de getters y setters (fechaFinContrato) se asumen heredados o definidos correctamente.
}