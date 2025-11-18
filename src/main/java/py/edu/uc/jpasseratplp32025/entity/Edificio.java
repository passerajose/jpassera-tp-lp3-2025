package py.edu.uc.jpasseratplp32025.entity; // O el paquete que uses para modelos/objetos de demo

import py.edu.uc.jpasseratplp32025.interfaces.Mapeable;
import py.edu.uc.jpasseratplp32025.model.Avatar;
import py.edu.uc.jpasseratplp32025.model.PosicionGPS;

public class Edificio implements Mapeable {

    private String nombre;
    private String direccion;
    private PosicionGPS ubicacionFija;

    public Edificio(String nombre, String direccion, PosicionGPS ubicacionFija) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ubicacionFija = ubicacionFija;
    }

    // --- Implementación de Mapeable ---

    @Override
    public PosicionGPS ubicarElemento() {
        // Retorna la ubicación fija del edificio
        return this.ubicacionFija;
    }

    @Override
    public Avatar obtenerImagen() {
        // Retorna un Avatar que representa el edificio
        Object imagen = new Object(); // Mock de un icono de edificio
        String nick = this.nombre;
        return new Avatar(imagen, nick);
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
}