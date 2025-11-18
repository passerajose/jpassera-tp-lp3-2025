package py.edu.uc.jpasseratplp32025.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import py.edu.uc.jpasseratplp32025.interfaces.Mapeable;
import py.edu.uc.jpasseratplp32025.entity.Contratista;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;
import py.edu.uc.jpasseratplp32025.entity.Vehiculo;
import py.edu.uc.jpasseratplp32025.entity.Edificio;
import py.edu.uc.jpasseratplp32025.model.PosicionGPS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MapeableFactory {

    private static final Logger log = LoggerFactory.getLogger(MapeableFactory.class);

    // Métodos de Creación de Objetos (simplificados, asumiendo que los MOCKs no necesitan log.info masivo)

    public static EmpleadoTiempoCompleto crearEmpleadoTiempoCompleto() {
        return new EmpleadoTiempoCompleto(
                "Marco", "Silva", 
                LocalDate.of(1985, 8, 10), 
                "4567890",
                new BigDecimal("6000000.00"), // salarioMensual
                "Desarrollo",                  // departamento
                LocalDate.now().plusYears(1)   // fechaFinContrato
        );
    }

    public static EmpleadoPorHora crearEmpleadoPorHora() {
        return new EmpleadoPorHora(
                "Lucas", "Vera", 
                LocalDate.of(1998, 11, 5), 
                "7123456",
                new BigDecimal("50000.00"),    // tarifaPorHora
                45,                            // horasTrabajadas
                LocalDate.now().plusMonths(6)  // fechaFinContrato
        );
    }

    public static Contratista crearContratista() {
        return new Contratista(
                "Carlos", "Ruiz", 
                LocalDate.of(1979, 3, 20), 
                "3123456",
                new BigDecimal("2500000.00"),  // montoPorProyecto
                3,                             // proyectosCompletados
                LocalDate.now().plusMonths(3)  // fechaFinContrato
        );
    }

    public static Vehiculo crearVehiculo() {
        return new Vehiculo(
                "ABC-123", "Furgoneta Sprinter",
                new PosicionGPS(-25.3050, -57.5500)
        );
    }

    public static Edificio crearEdificioSedeCentral() {
        return new Edificio(
                "Sede Central UC", "Avda. San Martín 1234",
                new PosicionGPS(-25.298818, -57.568461)
        );
    }

    /**
     * Crea y devuelve una lista completa de todos los elementos mapeables de ejemplo.
     */
    public static List<Mapeable> crearListaDemoCompleta() {
        log.info("Inicializando lista de elementos Mapeables para demostración.");
        List<Mapeable> lista = new ArrayList<>();
        lista.add(crearEmpleadoTiempoCompleto());
        lista.add(crearContratista());
        lista.add(crearEmpleadoPorHora());
        lista.add(crearVehiculo());
        lista.add(crearEdificioSedeCentral());
        return lista;
    }
}