import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.List;
import org.hibernate.stat.Statistics;

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

        configuracion.setProperty("hibernate.generate_statistics", "true"); // permite contar las sentencias SQL (demo 5)
        configuracion.addAnnotatedClass(Tutor.class);                       // registra la nueva entidad

        try (SessionFactory factory = configuracion.buildSessionFactory()) {
            prepararDatos(factory);
            listar(factory);
            //demo_DirtyChecking(factory);
            demo_N1(factory, true);

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
// PREPARACIÓN: vacía las tablas y carga los tutores y los alumnos de prueba.
static void prepararDatos(SessionFactory factory) {
    try (Session sesion = factory.openSession()) {
        Transaction tx = sesion.beginTransaction();
        try {
            // 1) Vaciar tablas (primero Alumno, por la clave ajena hacia Tutor)
            sesion.createMutationQuery("DELETE FROM Alumno").executeUpdate();
            sesion.createMutationQuery("DELETE FROM Tutor").executeUpdate();

            // 2) Tutores: id, nombre y apellidos
            sesion.persist(new Tutor(1, "Carmen", "García"));
            sesion.persist(new Tutor(2, "Pablo",  "Ruiz"));
            sesion.persist(new Tutor(3, "Laura",  "Sánchez"));
            sesion.persist(new Tutor(4, "Andrés", "Navarro"));

            // 3) Alumnos (los mismos de Principal, sin el 111)
            Alumno juan  = new Alumno(1, "Juan",  "Acceso a Datos", 7.9);
            Alumno ana   = new Alumno(2, "Ana",   "Acceso a Datos", 4.2);
            Alumno luis  = new Alumno(3, "Luis",  "Programación",   6.0);
            Alumno marta = new Alumno(4, "Marta", "Acceso a Datos", 9.8);

            // 4) Asignamos el tutor indicando su ID: el número de find(Tutor.class, N)
            //    es el ID del tutor. Devuelve el tutor ya persistido en esta sesión
            //    (no lanza ninguna consulta más).
            /*
            juan.setTutor(sesion.find(Tutor.class, 1));    // Juan  -> tutor 1
            ana.setTutor(sesion.find(Tutor.class, 1));     // Ana   -> tutor 1
            luis.setTutor(sesion.find(Tutor.class, 2));    // Luis  -> tutor 2
            marta.setTutor(sesion.find(Tutor.class, 2));   // Marta -> tutor 2
            */
            juan.setTutor(sesion.find(Tutor.class, 1));    // Juan  -> tutor 1
            ana.setTutor(sesion.find(Tutor.class, 2));     // Ana   -> tutor 1
            luis.setTutor(sesion.find(Tutor.class, 3));    // Luis  -> tutor 2
            marta.setTutor(sesion.find(Tutor.class, 4));   // Marta -> tutor 2

            // 5) Guardamos los alumnos
            sesion.persist(juan);
            sesion.persist(ana);
            sesion.persist(luis);
            sesion.persist(marta);

            tx.commit();
            System.out.println("Datos de prueba cargados");
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}


    // DEMO: DIRTY CHECKING
// Cargamos un alumno, cambiamos su nota con un setter y hacemos commit.
// No llamamos a ningún update(): Hibernate detecta el cambio él solo.

    static void demo_DirtyChecking(SessionFactory factory) {
        System.out.println("\n===== DEMO 4: DIRTY CHECKING =====");
        try (Session sesion = factory.openSession()) {
            Transaction tx = sesion.beginTransaction();
            try {
                // 1) find() carga la entidad y la deja en estado MANAGED.
                //    Hibernate guarda además una copia (snapshot) de sus valores.
                Alumno alumno = sesion.find(Alumno.class, 1);        // Juan, nota 7.9
                System.out.println("¿Está en estado Managed? " + sesion.contains(alumno));
                System.out.println("Nota antes: " + alumno.getNota());

                // 2) Modificamos con un setter. Aquí NO se emite ningún SQL.
                alumno.setNota(9.5);
                System.out.println(">>> setNota ejecutado: todavía NO hay UPDATE");

                // 3) En el commit se hace el flush: Hibernate compara el estado actual
                //    con el snapshot y, como difieren, emite el UPDATE.
                tx.commit();
                System.out.println(">>> commit terminado");
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
        // 4) Comprobación en una sesión NUEVA (método que ya tienes): la nota ya es 9.5.
        buscarPorId(factory, 1);
    }
    // DEMO 5B: PROBLEMA N+1.
// conFetch = false -> consulta normal; el tutor se carga "perezosamente", uno a uno.
// conFetch = true  -> JOIN FETCH; alumnos y tutores llegan en una sola consulta.
    static void demo_N1(SessionFactory factory, boolean conFetch) {
        System.out.println("\n===== DEMO N+1: " + (conFetch ? "CON" : "SIN") + " JOIN FETCH =====");

        // Contador de sentencias SQL de Hibernate (necesita hibernate.generate_statistics=true).
        Statistics stats = factory.getStatistics();
        stats.clear();                      // descarta lo contado en prepararDatos

        try (Session sesion = factory.openSession()) {
            String jpql = conFetch
                    ? "SELECT a FROM Alumno a JOIN FETCH a.tutor ORDER BY a.id"
                    : "SELECT a FROM Alumno a ORDER BY a.id";
            System.out.println("Consulta: " + jpql);

            List<Alumno> alumnos = sesion.createQuery(jpql, Alumno.class).getResultList();

            for (Alumno alumno : alumnos) {
                // getTutor() devuelve un proxy; getNombre() obliga a cargarlo si aún no está.
                System.out.println(alumno.getNombre() + " -> " + alumno.getTutor().getNombre());
                //System.out.println(alumno.getNombre());
            }
        }
        System.out.println("Sentencias SQL ejecutadas: " + stats.getPrepareStatementCount());
    }


//PRincipal
}

