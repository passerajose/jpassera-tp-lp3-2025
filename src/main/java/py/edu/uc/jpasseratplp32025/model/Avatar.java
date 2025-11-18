package py.edu.uc.jpasseratplp32025.model;

// Nota: Puedes usar java.awt.Image o simplemente Object, dependiendo de tus dependencias.
// Usaremos Object para evitar una dependencia gráfica innecesaria.
public class Avatar {
    private Object imagen;
    private String nick;

    // Constructor, Getters y Setters
    public Avatar(Object imagen, String nick) {
        this.imagen = imagen;
        this.nick = nick;
    }

    public Object getImagen() {
        return imagen;
    }

    public void setImagen(Object imagen) {
        this.imagen = imagen;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}