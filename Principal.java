import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class Principal {

    public static void main(String[] args) {
        Configuration configuracion = new Configuration();
        configuracion.setProperty(
                "hibernate.connection.driver_class", "org.sqlite.JDBC");
        configuracion.setProperty(
                "hibernate.connection.url", "jdbc:sqlite:orm.db");
        configuracion.setProperty(
                "hibernate.dialect",
                "org.hibernate.community.dialect.SQLiteDialect");
        configuracion.setProperty(
                "hibernate.hbm2ddl.auto", "update");

        configuracion.addAnnotatedClass(Alumno.class);

        try (SessionFactory factory =
                     configuracion.buildSessionFactory()) {

            try (Session sesion = factory.openSession()) {
                Transaction transaccion = sesion.beginTransaction();

                try {
                    Alumno alumno_nuevo = new Alumno(111, "Juan", "Accesp a DAtps", 7.9);
                    sesion.persist(alumno_nuevo);
//Listar alumnos
                    List<Alumno> lista_alumnos = sesion
                            .createQuery("FROM Alumno", Alumno.class)
                                    .getResultList();
                    for (Alumno a: lista_alumnos) {System.out.println(a);}

                    transaccion.commit();
                    System.out.println("Transaccion correcta");


                } catch (RuntimeException e) {
                    if (transaccion.isActive()) {
                        transaccion.rollback();
                    }
                    throw e;
                }


            }


        }

    }
}