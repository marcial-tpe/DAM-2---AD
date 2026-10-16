import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.List;
public class Principal {
    public static void main(String[] args) {
        Configuration configuracion = new Configuration();
        configuracion.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        configuracion.setProperty("hibernate.connection.url", "jdbc:sqlite:orm.db");
        configuracion.setProperty("hibernate.dialect",
                "org.hibernate.community.dialect.SQLiteDialect");
        configuracion.setProperty("hibernate.hbm2ddl.auto", "update");
        configuracion.setProperty("hibernate.show_sql", "true");
        configuracion.setProperty("hibernate.format_sql", "true");
        configuracion.addAnnotatedClass(Alumno.class);
        try (SessionFactory factory = configuracion.buildSessionFactory()) {

            System.out.println("\\n===== CREATE: INSERTAR =====");
            crear(factory, new Alumno(111, "Juan", "Acceso a Datos", 7.9));
            crear(factory, new Alumno(1, "Juan", "Acceso a Datos", 7.9));
            crear(factory, new Alumno(2, "Ana", "Acceso a Datos", 4.2));
            crear(factory, new Alumno(3, "Luis", "Programación", 6.0));
            crear(factory, new Alumno(4, "Marta", "Acceso a Datos", 9.8));
            System.out.println("\\n===== READ: LISTAR =====");
            listar(factory);
            System.out.println("\\n===== BUSCAR POR ID =====");
            buscarPorId(factory, 111);
            System.out.println("\\n===== UPDATE: ACTUALIZAR =====");
            actualizarNota(factory, 111, 9.5);
            buscarPorId(factory, 111);
            System.out.println("\\n===== DELETE: ELIMINAR =====");
            borrar(factory, 111);
            listar(factory);
            System.out.println("\\n===== UPDATE MASIVO =====");
            int subidos = subirNota(factory, "Acceso a Datos", 0.5);
            System.out.println(subidos + " alumnos actualizados");
            listar(factory);
            System.out.println("\\n===== DELETE MASIVO =====");
            int borrados = borrarSuspensos(factory, 5.0);
            System.out.println(borrados + " alumnos eliminados");
            listar(factory);
        }
    }
    // CREATE: inserta solo si el identificador todavía no existe.
    static void crear(SessionFactory factory, Alumno alumno) {
        try (Session sesion = factory.openSession()) {
            Transaction tx = sesion.beginTransaction();
            try {
                Alumno existente = sesion.find(Alumno.class, alumno.getId());
                if (existente != null) {
                    System.out.println("Ya existe el alumno con ID " + alumno.getId());
                } else {
                    sesion.persist(alumno);
                    System.out.println("Alumno insertado: " + alumno);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
    // READ: lista todos los alumnos.
    static void listar(SessionFactory factory) {
        try (Session sesion = factory.openSession()) {
            List<Alumno> lista = sesion.createQuery(
                    "FROM Alumno ORDER BY id", Alumno.class
            ).getResultList();
            if (lista.isEmpty()) {
                System.out.println("No hay alumnos.");
            } else {
                for (Alumno alumno : lista) System.out.println(alumno);
            }
        }
    }
    // READ: busca por clave primaria.
    static void buscarPorId(SessionFactory factory, int id) {
        try (Session sesion = factory.openSession()) {
            Alumno alumno = sesion.find(Alumno.class, id);
            if (alumno != null) {
                System.out.println("Alumno encontrado:");
                System.out.println(alumno);
            } else {
                System.out.println("No existe el alumno con ID " + id);
            }
        }
    }
    // UPDATE: Hibernate detecta el cambio mediante dirty checking.
    static void actualizarNota(SessionFactory factory, int id, double nuevaNota) {
        try (Session sesion = factory.openSession()) {
            Transaction tx = sesion.beginTransaction();
            try {
                Alumno alumno = sesion.find(Alumno.class, id);
                if (alumno != null) {
                    alumno.setNota(nuevaNota);
                    System.out.println("Nota actualizada para el alumno " + id);
                } else {
                    System.out.println("No existe el alumno con ID " + id);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
    // DELETE: busca la entidad antes de eliminarla.
    static void borrar(SessionFactory factory, int id) {
        try (Session sesion = factory.openSession()) {
            Transaction tx = sesion.beginTransaction();
            try {
                Alumno alumno = sesion.find(Alumno.class, id);
                if (alumno != null) {
                    sesion.remove(alumno);
                    System.out.println("Alumno eliminado con ID " + id);
                } else {
                    System.out.println("No existe el alumno con ID " + id);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
    // UPDATE masivo con HQL: evita que la nota supere 10.
    static int subirNota(SessionFactory factory, String asignatura, double incremento) {
        if (incremento < 0 || incremento > 10) {
            throw new IllegalArgumentException("El incremento debe estar entre 0 y 10.");
        }
        try (Session sesion = factory.openSession()) {
            Transaction tx = sesion.beginTransaction();
            try {
                int filas = sesion.createMutationQuery(
                                "UPDATE Alumno SET nota = nota + :inc " +
                                        "WHERE asignatura = :asig AND nota <= :tope"
                        )
                        .setParameter("inc", incremento)
                        .setParameter("asig", asignatura)
                        .setParameter("tope", 10.0 - incremento)
                        .executeUpdate();
                tx.commit();
                return filas;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
    // DELETE masivo con HQL: elimina las notas inferiores al mínimo indicado.
    static int borrarSuspensos(SessionFactory factory, double notaMinima) {
        try (Session sesion = factory.openSession()) {
            Transaction tx = sesion.beginTransaction();
            try {
                int filas = sesion.createMutationQuery(
                                "DELETE FROM Alumno WHERE nota < :minima"
                        )
                        .setParameter("minima", notaMinima)
                        .executeUpdate();
                tx.commit();
                return filas;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}

