//package py.edu.uc.jpasseratplp32025;
//
//import lombok.extern.slf4j.Slf4j;
//import py.edu.uc.jpasseratplp32025.entity.*;
//import py.edu.uc.jpasseratplp32025.dto.SolicitudPermisoDto;
//import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;
//import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
//import py.edu.uc.jpasseratplp32025.util.NominaUtils;
//
//import java.time.LocalDate;
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//public class NuevasFuncionesDemoApp {
//
//    public static void main(String[] args) {
//        log.info("=== INICIANDO DEMO DE NUEVAS FUNCIONALIDADES ===");
//
//        // 1. Crear instancias de prueba
//        EmpleadoTiempoCompleto empleado = new EmpleadoTiempoCompleto(
//            "Ana", "Gómez",
//            LocalDate.of(1990, 5, 15),
//            "1111111",
//            new BigDecimal("3000000"),
//            "Ventas"
//        );
//
//        Gerente gerente = new Gerente(
//            "Carlos", "Ramírez",
//            LocalDate.of(1985, 10, 20),
//            "2222222",
//            LocalDate.now().minusYears(5),
//            40,
//            "Finanzas"
//        );
//
//        List<PersonaJpa> empleados = Arrays.asList(empleado, gerente);
//
//        // 2. Probar NominaUtils
//        log.info("\n=== Probando NominaUtils ===");
//        int totalDias = NominaUtils.totalDiasSolicitados(
//            empleados,
//            p -> p instanceof Empleado ? ((Empleado)p).consultarDiasVacacionesDisponibles(p.getNumeroDeCedula()) : 0
//        );
//        log.info("Total días disponibles: {}", totalDias);
//
//        String reporteJson = NominaUtils.generarReporteJsonPorDias(
//            empleados,
//            5,
//            p -> p instanceof Empleado ? ((Empleado)p).consultarDiasVacacionesDisponibles(p.getNumeroDeCedula()) : 0
//        );
//        log.info("Reporte JSON:\n{}", reporteJson);
//
//        // 3. Probar solicitudes de permisos
//        log.info("\n=== Probando Solicitudes de Permisos ===");
//
//        // 3.1 Empleado regular intentando solicitar más de 20 días
//        try {
//            log.info("Empleado regular intenta solicitar 25 días...");
//            empleado.solicitarPermiso(
//                LocalDate.now().plusDays(1),
//                LocalDate.now().plusDays(25),
//                "VACACIONES",
//                empleado.getNumeroDeCedula()
//            );
//        } catch (PermisoNoConcedidoException e) {
//            log.info("Correcto: Permiso denegado para empleado regular - {}", e.getMessage());
//        }
//
//        // 3.2 Gerente solicitando más de 20 días (debe ser permitido)
//        try {
//            log.info("Gerente intenta solicitar 25 días...");
//            gerente.solicitarPermiso(
//                LocalDate.now().plusDays(1),
//                LocalDate.now().plusDays(25),
//                "VACACIONES",
//                gerente.getNumeroDeCedula()
//            );
//            log.info("Correcto: Permiso aprobado para gerente");
//        } catch (PermisoNoConcedidoException e) {
//            log.error("Error: El gerente debería poder solicitar más de 20 días - {}", e.getMessage());
//        }
//
//        // 4. Probar mappers
//        log.info("\n=== Probando Mappers ===");
//        // Aquí podrías probar los mappers si tienes acceso a los servicios
//
//        log.info("=== FIN DE LA DEMO ===");
//    }
//}