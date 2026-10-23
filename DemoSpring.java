package org.example.u3orm;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
public class DemoSpring {
    public static void main(String[] args) {
        SpringApplication.run(DemoSpring.class, args);
    }
    private static void mostrar(String titulo, List<Alumno> lista) {
        System.out.println(titulo);
        for (Alumno alumno : lista) {
            System.out.println(
                    alumno.getNombre() + " (" + alumno.getNota() + ")"
            );
        }
    }
    @Bean
    CommandLineRunner demo(
            AlumnoRepository alumnos,
            TutorRepository tutores) {
        return args -> {
// Crear los tutores.
            Tutor t1 = tutores.save(new Tutor(1, "Carmen", "García"));
            Tutor t2 = tutores.save(new Tutor(2, "Pablo", "Ruiz"));
// Crear los alumnos y asignarles tutor.
            Alumno juan = new Alumno(1, "Juan", "Acceso a Datos", 7.9);
            juan.setTutor(t1);
            Alumno ana = new Alumno(2, "Ana", "Acceso a Datos", 4.2);
            ana.setTutor(t1);
            Alumno luis = new Alumno(3, "Luis", "Programación", 6.0);
            luis.setTutor(t2);
            Alumno marta = new Alumno(4, "Marta", "Acceso a Datos", 9.8);
            marta.setTutor(t2);
            alumnos.saveAll(List.of(juan, ana, luis, marta));
// Consultas básicas heredadas de JpaRepository.
            System.out.println("===== DATOS CARGADOS =====");
            System.out.println("Total de alumnos: " + alumnos.count());
            System.out.println("Alumno con ID 3: "
                    + alumnos.findById(3).orElse(null));
            mostrar("===== LISTADO DE ALUMNOS =====", alumnos.findAll());
        };
    }
}
