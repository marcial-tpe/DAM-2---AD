import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Entidad auxiliar: solo sirve para tener una relación con Alumno y poder demostrar el N+1.
@Entity
@Table(name = "Tutor")
public class Tutor {
    @Id
    private int id;                          // clave asignada a mano, igual que en Alumno

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    public Tutor() { }                       // Hibernate exige un constructor sin argumentos

    public Tutor(int id, String nombre, String apellidos) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }

    @Override
    public String toString() {
        return "Tutor [ id= " + id + " - Nombre: " + nombre + " " + apellidos + "]";
    }
}
