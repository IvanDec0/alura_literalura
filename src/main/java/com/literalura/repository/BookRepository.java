package com.literalura.repository;

import com.literalura.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);  // Para buscar libros por su título

    @Query("SELECT b FROM Book b WHERE b.language LIKE %?1%")
    List<Book> findByLanguage(String language);  // Para buscar libros por idioma

    @Query("SELECT COUNT(b) FROM Book b WHERE b.language = ?1")
    long countByLanguage(String language);
}