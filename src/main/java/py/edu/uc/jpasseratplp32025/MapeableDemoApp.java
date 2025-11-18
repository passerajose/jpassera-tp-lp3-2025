package py.edu.uc.jpasseratplp32025;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import py.edu.uc.jpasseratplp32025.interfaces.Mapeable;
import py.edu.uc.jpasseratplp32025.model.PosicionGPS;
import py.edu.uc.jpasseratplp32025.util.MapeableProcessor;
import py.edu.uc.jpasseratplp32025.util.MapeableFactory;

import java.util.List;

public class MapeableDemoApp {

    private static final Logger log = LoggerFactory.getLogger(MapeableDemoApp.class);

    public static void main(String[] args) {
        log.info("--- DEMOSTRACIÓN DEL POLIMORFISMO DE LA INTERFAZ MAPEABLE ---");

        // 1. CREACIÓN DE INSTANCIAS usando la FÁBRICA
        List<Mapeable> elementosMapeables = MapeableFactory.crearListaDemoCompleta();

        // 2. LLAMADA A LA UTILIDAD (Procesamiento)
        MapeableProcessor.procesarYMostrar(elementosMapeables);

        // 3. Demostración de Reutilización (Usando log.info())
        log.info("[Ejemplo de Reutilización de Ubicación]");

        // Creamos una instancia específica para la demostración
        Mapeable vehiculo = MapeableFactory.crearVehiculo();
        PosicionGPS ubicacionVehiculo = vehiculo.ubicarElemento();

        // Necesitamos casting solo para obtener propiedades fuera de la interfaz Mapeable (como el modelo)
        log.info("El vehículo ({}) está en Lat: {}, Lon: {}",
                ((py.edu.uc.jpasseratplp32025.entity.Vehiculo) vehiculo).getModelo(),
                ubicacionVehiculo.getLatitude(),
                ubicacionVehiculo.getLongitude());
    }
}