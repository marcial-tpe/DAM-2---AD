import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Alumno")
public class Alumno {
    @Id   private int id;
    @Column(nullable = false)    private String nombre;
    @Column(nullable = false)   private String asignatura;
    @Column(nullable = false)    private double nota;

    public Alumno() {
    }

    public Alumno(int id, String nombre, String asignatura, double nota) {
        this.id = id;
        this.nombre = nombre;
        this.asignatura = asignatura;
        this.nota = nota;
    }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getAsignatura() { return asignatura; }
    public double getNota() { return nota; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
    public void setNota(double nota) { this.nota = nota; }

    @Override
    public String toString() {
        return "Alumno [ id= " + id
                + " - Nombre: " + nombre
                + " - Assignatura: " + asignatura
                + " - Nota: " + nota + "]";
    }
}