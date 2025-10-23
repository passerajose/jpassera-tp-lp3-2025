package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class NominaService {

    private static final Logger log = LoggerFactory.getLogger(NominaService.class);

    // Asumimos que PersonaRepository extiende JpaRepository<PersonaJpa, Long>
    private final PersonaRepository personaRepository;

    @Autowired
    public NominaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
        log.info("NominaService inicializado para gestión centralizada de reportes.");
    }

    // ========================================================================
    // DEMOSTRACIÓN DE POLIMORFISMO
    // ========================================================================

    /**
     * Genera un reporte completo de todos los empleados utilizando polimorfismo.
     * Itera sobre la lista de PersonaJpa y llama a métodos que se resuelven
     * en tiempo de ejecución para cada subclase (TiempoCompleto, PorHora, Contratista).
     *
     * @return Una lista de Strings con el reporte detallado.
     */
    public List<String> generarReporteCompleto() {
        log.info("Iniciando la generación del reporte completo de nómina.");

        // Obtener todas las personas de la tabla única.
        // Spring/Hibernate se encarga de instanciar la subclase correcta (polimorfismo).
        List<PersonaJpa> todasLasPersonas = personaRepository.findAll();
        List<String> reporte = new ArrayList<>();

        reporte.add("=====================================================================================================");
        reporte.add("                     REPORTE POLIMÓRFICO DE NÓMINA Y VALIDACIONES");
        reporte.add("=====================================================================================================");

        int totalPersonas = todasLasPersonas.size();
        if (totalPersonas == 0) {
            reporte.add("La base de datos no contiene registros de empleados.");
            return reporte;
        }

        for (PersonaJpa persona : todasLasPersonas) {
            String tipoClase = persona.getClass().getSimpleName();
            String ci = persona.getNumeroDeCedula();

            reporte.add("-----------------------------------------------------------------------------------------------------");
            reporte.add(String.format("Persona: %s (C.I.: %s, Tipo: %s)", persona.getNombre(), ci, tipoClase));

            // 1. Llamar obtenerInformacionCompleta() (Polimorfismo en acción)
            String info = persona.obtenerInformacionCompleta();
            reporte.add(String.format("  [INFO COMPLETA]: %s", info));

            // 2. Validar datos específicos (Polimorfismo en acción)
            boolean esValido = persona.validarDatosEspecificos();
            reporte.add(String.format("  [VALIDACIÓN DE DATOS]: %s", esValido ? "✅ OK" : "❌ FALLÓ (Reglas específicas no cumplidas)"));

            // 3. Calcular impuestos (Polimorfismo en acción)
            try {
                BigDecimal impuestos = persona.calcularImpuestos();
                reporte.add(String.format("  [IMPUESTOS CALCULADOS]: %s",
                        (impuestos != null ? impuestos.setScale(2, BigDecimal.ROUND_HALF_UP).toString() : "N/A (No aplica o error de cálculo)")));
            } catch (UnsupportedOperationException e) {
                reporte.add("  [IMPUESTOS CALCULADOS]: N/A (Método no implementado o no aplica para este tipo)");
            } catch (Exception e) {
                reporte.add("  [IMPUESTOS CALCULADOS]: ERROR al calcular: " + e.getMessage());
            }

            log.info("Reporte generado para {} con C.I. {}. Validez: {}", tipoClase, ci, esValido);
        }
        reporte.add("=====================================================================================================");
        return reporte;
    }
}