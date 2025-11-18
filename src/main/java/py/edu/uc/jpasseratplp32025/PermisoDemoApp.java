//package py.edu.uc.jpasseratplp32025;
//
//// Importaciones necesarias
//import lombok.extern.slf4j.Slf4j;
//
//import py.edu.uc.jpasseratplp32025.entity.Empleado;
//import py.edu.uc.jpasseratplp32025.entity.Gerente; // Importar la clase Gerente
//import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
//import py.edu.uc.jpasseratplp32025.interfaces.AprobadorGerencial; // Importar la interfaz de Aprobación
//import py.edu.uc.jpasseratplp32025.interfaces.Permisionable;
//import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
//
//import java.time.LocalDate;
//
///**
// * Clase de demostración para probar las interfaces Permisionable y AprobadorGerencial.
// */
//@Slf4j
//public class PermisoDemoApp {
//
//    public static void main(String[] args) {
//        log.info("--- DEMOSTRACIÓN DE PERMISIONABLE Y APROBADOR GERENCIAL ---\n");
//
//        // La fecha actual de simulación
//        LocalDate hoy = LocalDate.now();
//
//        // 1. Empleado apto (Permisionable): Días > 1 año y 20 días de saldo
//        Permisionable empleadoApto = new Empleado("María", "Solís", LocalDate.of(1995, 3, 10), "4567890", hoy.minusYears(2), 20);
//
//        // 2. Empleado nuevo (Permisionable): Sin antigüedad suficiente
//        Permisionable empleadoNuevo = new Empleado("Pedro", "Gómez", LocalDate.of(1998, 7, 20), "7123456", hoy.minusMonths(6), 10);
//
//        // 3. Empleado sin saldo (Permisionable): Días insuficientes
//        Permisionable empleadoSinSaldo = new Empleado("Ana", "Pérez", LocalDate.of(1980, 1, 1), "3123456", hoy.minusYears(5), 5);
//
//        // 4. Gerente (AprobadorGerencial y Permisionable): Rol con autoridad y reglas flexibles
//        AprobadorGerencial gerenteAprobador = new Gerente("Juan", "Vera", LocalDate.of(1975, 5, 20), "1011121", hoy.minusYears(10), 40, "Finanzas");
//
//
//        log.info("\n=======================================================");
//        log.info("        PRUEBAS DE SOLICITUDES (PERMISIONABLE)");
//        log.info("=======================================================");
//
//        // --- CASO 1: Éxito (Permiso Especial Matrimonio) ---
//        probarSolicitud(empleadoApto, hoy, hoy.plusDays(3), "MATRIMONIO", 1001);
//
//        // --- CASO 2: Falla por Antigüedad (Denegación por ley paraguaya) ---
//        probarSolicitud(empleadoNuevo, hoy.plusMonths(1), hoy.plusMonths(1).plusDays(15), "VACACIONES", 1002);
//
//        // --- CASO 3: Falla por Saldo Insuficiente ---
//        probarSolicitud(empleadoSinSaldo, hoy.plusDays(10), hoy.plusDays(25), "VACACIONES", 1003);
//
//        // --- CASO 4: Empleado Regular Excede Límite de 20 días (DEBE FALLAR) ---
//        probarSolicitud(empleadoApto, hoy.plusDays(100), hoy.plusDays(125), "VACACIONES", 1004); // 26 días
//
//        // --- CASO 5: Gerente Excede Límite de 20 días (DEBE SER EXITOSO) ---
//        probarSolicitud(gerenteAprobador, hoy.plusDays(200), hoy.plusDays(225), "VACACIONES", 3001); // 26 días
//
//        log.info("\n=======================================================");
//        log.info("      PRUEBAS DE APROBACIÓN (APROBADOR GERENCIAL)");
//        log.info("=======================================================");
//
//        // --- CASO 6: Gerente Aprueba una Solicitud (Éxito) ---
//        probarAprobacion(gerenteAprobador, 4001, true, "El suplente fue debidamente entrenado.");
//
//        // --- CASO 7: Gerente Rechaza una Solicitud (Falla intencional) ---
//        probarAprobacion(gerenteAprobador, 4002, false, "El proyecto requiere la presencia inmediata del solicitante.");
//
//        log.info("\n--- FIN DE LA DEMOSTRACIÓN ---");
//        // FIX: Se realiza un casting explícito a PersonaJpa para acceder a getNumeroDeCedula()
//        log.info("Días restantes de Gerente (después de usar 26 días): {}",
//                gerenteAprobador.consultarDiasVacacionesDisponibles(((PersonaJpa) gerenteAprobador).getNumeroDeCedula()));
//    }
//
//    /**
//     * Prueba la solicitud de permiso e imprime el resultado usando log.
//     */
//    private static void probarSolicitud(Permisionable permisionable, LocalDate inicio, LocalDate fin, String tipo, long idSolicitud) {
//        // Se requiere el casting a PersonaJpa para obtener el nombre y cédula
//        String nombreEmpleado = ((PersonaJpa) permisionable).getNombre();
//        String codigoEmpleado = ((PersonaJpa) permisionable).getNumeroDeCedula();
//
//        // Calcular días solicitados
//        long diasSolicitados = java.time.temporal.ChronoUnit.DAYS.between(inicio, fin) + 1;
//
//        log.info("\n[Prueba de Solicitud - {}: {}]",
//                (permisionable instanceof AprobadorGerencial ? "Gerente" : "Empleado"),
//                nombreEmpleado);
//        log.info("Días disponibles antes: {}. Solicitando {} días por {}...",
//                permisionable.consultarDiasVacacionesDisponibles(codigoEmpleado), diasSolicitados, tipo);
//
//        try {
//            permisionable.solicitarPermiso(inicio, fin, tipo, codigoEmpleado);
//            log.info("✅ Solicitud Inicial Aprobada. Pendiente de Aprobación Final.");
//
//        } catch (PermisoNoConcedidoException e) {
//            log.error("❌ Solicitud Rechazada: {}", e.getMessage());
//            log.error("   Motivo de Rechazo: {}", e.getMotivoRechazo());
//        }
//        log.info("Días disponibles después: {}.", permisionable.consultarDiasVacacionesDisponibles(codigoEmpleado));
//    }
//
//    /**
//     * Prueba la aprobación gerencial, que solo puede ser ejecutada por un AprobadorGerencial.
//     */
//    private static void probarAprobacion(AprobadorGerencial aprobador, long idSolicitud, boolean aprobado, String notas) {
//        String nombreAprobador = ((PersonaJpa) aprobador).getNombre();
//        String estado = aprobado ? "APROBACIÓN" : "RECHAZO";
//
//        log.info("\n[Prueba de Aprobación Final - {}: {}]", estado, nombreAprobador);
//
//        try {
//            aprobador.procesarAprobacionGerencial(idSolicitud, aprobado, notas);
//            log.info("✅ Solicitud #{} procesada y APROBADA con éxito por Gerencia.", idSolicitud);
//        } catch (PermisoNoConcedidoException e) {
//            log.error("❌ Solicitud #{} procesada y RECHAZADA. {}", idSolicitud, e.getMessage());
//            log.error("   Motivo de Rechazo Gerencial: {}", e.getMotivoRechazo());
//        }
//    }
//}