package org.example.u3orm;

import org.springframework.data.jpa.repository.JpaRepository;
public interface TutorRepository
        extends JpaRepository<Tutor, Integer> {
}
