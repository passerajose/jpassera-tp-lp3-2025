package py.edu.uc.jpasseratplp32025.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import py.edu.uc.jpasseratplp32025.interfaces.Mapeable;
import py.edu.uc.jpasseratplp32025.model.PosicionGPS;
import py.edu.uc.jpasseratplp32025.model.Avatar;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.entity.Vehiculo;
import py.edu.uc.jpasseratplp32025.entity.Edificio;

import java.util.List;

public class MapeableProcessor {

    private static final Logger log = LoggerFactory.getLogger(MapeableProcessor.class);

    /**
     * Procesa e imprime la información de ubicación y avatar para una lista de objetos Mapeable.
     * Reemplaza System.out.println por log.info().
     * @param elementosMapeables Lista de objetos que implementan la interfaz Mapeable.
     */
    public static void procesarYMostrar(List<Mapeable> elementosMapeables) {

        log.info("[Procesando Elementos Mapeables]");
        log.info("------------------------------------------------------------------");

        for (Mapeable elemento : elementosMapeables) {
            // 1. Llamadas Polimórficas
            PosicionGPS pos = elemento.ubicarElemento();
            Avatar avatar = elemento.obtenerImagen();

            // 2. Determinar la Identificación
            String identificador = determinarIdentificador(elemento);

            // 3. Impresión usando log.info()
            log.info("Elemento: {}", identificador);
            log.info(" -> Ubicación: Lat: {}, Lon: {}", pos.getLatitude(), pos.getLongitude());
            log.info(" -> Avatar Nick: {}", avatar.getNick());
            log.info("------------------------------------------------------------------");
        }
    }

    /**
     * Método auxiliar para determinar una identificación legible del objeto.
     */
    private static String determinarIdentificador(Mapeable elemento) {
        if (elemento instanceof PersonaJpa) {
            PersonaJpa persona = (PersonaJpa) elemento;
            return String.format("Persona: %s (C.I.: %s)", persona.getNombre(), persona.getNumeroDeCedula());
        } else if (elemento instanceof Vehiculo) {
            Vehiculo vehiculo = (Vehiculo) elemento;
            return String.format("Vehículo: %s (Placa: %s)", vehiculo.getModelo(), vehiculo.getPlaca());
        } else if (elemento instanceof Edificio) {
            Edificio edificio = (Edificio) elemento;
            return String.format("Edificio: %s", edificio.getNombre());
        } else {
            return "Elemento Desconocido: " + elemento.getClass().getSimpleName();
        }
    }
}