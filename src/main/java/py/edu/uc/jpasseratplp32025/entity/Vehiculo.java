package py.edu.uc.jpasseratplp32025.entity; // O el paquete que uses para modelos/objetos de demo

import py.edu.uc.jpasseratplp32025.interfaces.Mapeable;
import py.edu.uc.jpasseratplp32025.model.Avatar;
import py.edu.uc.jpasseratplp32025.model.PosicionGPS;

public class Vehiculo implements Mapeable {

    private String placa;
    private String modelo;
    private PosicionGPS ubicacionActual;

    public Vehiculo(String placa, String modelo, PosicionGPS ubicacionActual) {
        this.placa = placa;
        this.modelo = modelo;
        this.ubicacionActual = ubicacionActual;
    }

    // --- Implementación de Mapeable ---

    @Override
    public PosicionGPS ubicarElemento() {
        // Retorna la ubicación actual del vehículo
        return this.ubicacionActual;
    }

    @Override
    public Avatar obtenerImagen() {
        // Retorna un Avatar que representa el vehículo
        Object imagen = new Object(); // Mock de una imagen de coche
        String nick = this.modelo + " (" + this.placa + ")";
        return new Avatar(imagen, nick);
    }

    // Getters
    public String getPlaca() { return placa; }
    public String getModelo() { return modelo; }
}