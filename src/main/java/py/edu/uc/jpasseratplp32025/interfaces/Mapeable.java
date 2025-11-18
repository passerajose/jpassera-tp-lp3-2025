package py.edu.uc.jpasseratplp32025.interfaces;

import py.edu.uc.jpasseratplp32025.model.PosicionGPS;
import py.edu.uc.jpasseratplp32025.model.Avatar;

public interface Mapeable {

    /**
     * Define la capacidad de un elemento para ser ubicado en un mapa.
     * @return La posición GPS del elemento.
     */
    PosicionGPS ubicarElemento();

    /**
     * Define la capacidad de un elemento para obtener su representación visual/imagen.
     * @return El objeto Avatar que contiene la imagen y el nick.
     */
    Avatar obtenerImagen();
}