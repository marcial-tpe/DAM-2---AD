package org.example.u3orm;

import org.springframework.data.jpa.repository.JpaRepository;
public interface AlumnoRepository
        extends JpaRepository<Alumno, Integer> {
}
