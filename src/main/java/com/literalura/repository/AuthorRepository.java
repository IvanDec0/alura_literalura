package com.literalura.repository;

import com.literalura.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByName(String name);  // Para buscar un autor por su nombre
    List<Author> findByBirthYearBeforeAndDeathYearAfterOrDeathYearIsNull(int birthYear, int deathYear);
}
