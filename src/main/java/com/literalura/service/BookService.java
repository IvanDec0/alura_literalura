package com.literalura.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.literalura.entity.Author;
import com.literalura.entity.Book;
import com.literalura.repository.AuthorRepository;
import com.literalura.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookService {

    private final HttpService httpService;
    private final ObjectMapper objectMapper;
    private final Scanner scanner;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public BookService(HttpService httpService, ObjectMapper objectMapper,
                       AuthorRepository authorRepository, BookRepository bookRepository) {
        this.httpService = httpService;
        this.objectMapper = objectMapper;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.scanner = new Scanner(System.in);
    }

    // Guardar libro y asociar autores
    @Transactional
    public Book saveBook(Book book) {
        List<Author> authors = new ArrayList<>();
        book.getAuthors().forEach(author -> {
            Author existingAuthor = authorRepository.findByName(author.getName());
            Author saved_author;
            if (existingAuthor != null) {
                saved_author = existingAuthor;
            } else {
                saved_author = authorRepository.save(author);
            }
            authors.add(saved_author);
        });
        book.setLanguage(book.getLanguages().get(0));
        book.setAuthors(authors);
        return bookRepository.save(book);
    }

    // Obtener todos los libros de la base de datos
    public List<Book> getAllSearchedBooks() {
        return bookRepository.findAll();
    }

    public Book searchBookByTitle(String title) {
        Map response = httpService.sendRequest();
        try {
            // Convertir la respuesta a una lista de libros
            List<Book> books = objectMapper.convertValue(response.get("results"), new TypeReference<List<Book>>() {});

            // Usar Stream para filtrar los libros por título, limpiando espacios
            Optional<Book> filteredBook = books.stream()
                    .filter(book -> book.getTitle() != null && book.getTitle().trim().contains(title.trim())) // Filtrar por título con trim
                    .findFirst(); // Retener el primer libro encontrado

            // Si encontramos un libro, lo guardamos y lo devolvemos
            if (filteredBook.isPresent()) {
                Book book = filteredBook.get();
                saveBook(book); // Guardar el libro en la base de datos
                return book;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Buscar libros por idioma
    public List<Book> searchBooksByLanguage(String language) {
        List<Book> filteredBooks = new ArrayList<>();
        List<Book> searchedBooks = getAllSearchedBooks();
        for (Book book : searchedBooks) {
            if (book.getLanguage() != null && !book.getLanguage().isEmpty()) {
                if (book.getLanguage().equalsIgnoreCase(language)) {
                    filteredBooks.add(book);
                }
            }
        }
        return filteredBooks;
    }

    // Mostrar todos los libros
    public void showAllBooks() {
        List<Book> books = getAllSearchedBooks();
        if (books != null && !books.isEmpty()) {
            System.out.println("----- Lista de Libros -----");
            for (Book book : books) {
                StringBuilder authors = new StringBuilder();
                if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                    for (Author author : book.getAuthors()) {
                        authors.append(author.getName()).append(", ");
                    }
                    if (authors.length() > 0) {
                        authors.setLength(authors.length() - 2);
                    }
                } else {
                    authors.append("No disponibles");
                }
                System.out.println("ID: " + book.getId() + ", Título: " + book.getTitle() + ", Lenguaje: " + book.getLanguage() + ", Autores: " + authors.toString());
            }
        } else {
            System.out.println("No se han encontrado libros.");
        }
    }

    // Mostrar detalles de un libro por ID
    public void showBookDetails() {
        System.out.print("Ingrese el ID del libro: ");
        String id = scanner.nextLine();
        Book book = getBook(id);
        if (book != null) {
            System.out.println("----- Detalles del Libro -----");
            System.out.println("ID: " + book.getId());
            System.out.println("Título: " + book.getTitle());

            StringBuilder authors = new StringBuilder();
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                for (Author author : book.getAuthors()) {
                    authors.append(author.getName()).append(", ");
                }
                if (authors.length() > 0) {
                    authors.setLength(authors.length() - 2);
                }
            } else {
                authors.append("No disponibles");
            }
            System.out.println("Autores: " + authors.toString());

            StringBuilder languages = new StringBuilder();
            if (book.getLanguages() != null && !book.getLanguages().isEmpty()) {
                for (String language : book.getLanguages()) {
                    languages.append(language).append(", ");
                }
                if (languages.length() > 0) {
                    languages.setLength(languages.length() - 2);
                }
            } else {
                languages.append("No disponibles");
            }
            System.out.println("Lenguajes: " + languages.toString());

            System.out.println("Descargas: " + (book.getDownloadCount() != null ? book.getDownloadCount() : "No disponibles"));
        } else {
            System.out.println("Libro no encontrado con el ID: " + id);
        }
    }

    // Obtener libro desde la API
    public Book getBook(String id) {
        Map response = httpService.sendRequest(id);
        try {
            Book book = objectMapper.convertValue(response, new TypeReference<Book>() {});
            List<String> languages = book.getLanguages();
            if (languages != null && !languages.isEmpty()) {
                book.setLanguages(Collections.singletonList(languages.get(0)));
            }
            saveBook(book);
            return book;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buscar libros por título desde la API
    public void searchBookByTitle() {
        System.out.print("Ingrese el título del libro: ");
        String title = scanner.nextLine();
        Book book = searchBookByTitle(title);

        if (book != null) {
            System.out.println("----- Libro Encontrado -----");
            System.out.println("Título: " + book.getTitle());
            System.out.println("Autor: " + (book.getAuthors() != null ? book.getAuthors().get(0).getName() : "No disponible"));
            System.out.println("Idioma: " + (book.getLanguages() != null ? book.getLanguages().get(0) : "No disponible"));
            System.out.println("Número de Descargas: " + (book.getDownloadCount() != null ? book.getDownloadCount() : "No disponible"));
        } else {
            System.out.println("No se encontraron libros con el título: " + title);
        }
    }

    // Filtrar libros por idioma
    public void filterBooksByLanguage() {
        System.out.print("Ingrese el idioma: ");
        String language = scanner.nextLine();
        List<Book> filteredBooks = searchBooksByLanguage(language);

        if (!filteredBooks.isEmpty()) {
            System.out.println("----- Libros en el idioma: " + language + " -----");
            for (Book book : filteredBooks) {
                System.out.println("Título: " + book.getTitle() + ", Autor: " + (book.getAuthors() != null ? book.getAuthors().get(0).getName() : "No disponible") +
                        ", Idioma: " + (book.getLanguages() != null ? book.getLanguages().get(0) : "No disponible") +
                        ", Descargas: " + (book.getDownloadCount() != null ? book.getDownloadCount() : "No disponible"));
            }
        } else {
            System.out.println("No se encontraron libros en el idioma: " + language);
        }
    }

    // Mostrar cantidad de libros por idioma
    public void showBooksCountByLanguage() {
        System.out.print("Ingrese el idioma: ");
        String language = scanner.nextLine();
        long count = bookRepository.countByLanguage(language);
        System.out.println("Número de libros en el idioma " + language + ": " + count);
    }

    // Obtener todos los autores
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // Mostrar todos los autores
    public void showAllAuthors() {
        List<Author> authors = getAllAuthors();
        if (authors != null && !authors.isEmpty()) {
            System.out.println("----- Lista de Autores -----");
            for (Author author : authors) {
                System.out.println("Nombre: " + author.getName() + ", Año de nacimiento: " + author.getBirthYear() +
                        ", Año de fallecimiento: " + (author.getDeathYear() != null ? author.getDeathYear() : "Aún vivo"));
            }
        } else {
            System.out.println("No se han encontrado autores.");
        }
    }

    // Listar autores vivos en un año
    public List<Author> getAuthorsAliveInYear(int year) {
        return authorRepository.findByBirthYearBeforeAndDeathYearAfterOrDeathYearIsNull(year, year);
    }

    // Mostrar autores vivos en un determinado año
    public void showAuthorsAliveInYear() {
        System.out.print("Ingrese el año: ");
        try {
            int year = Integer.parseInt(scanner.nextLine());
            List<Author> aliveAuthors = getAuthorsAliveInYear(year);
            if (!aliveAuthors.isEmpty()) {
                System.out.println("----- Autores vivos en el año " + year + " -----");
                for (Author author : aliveAuthors) {
                    System.out.println(author.getName());
                }
            } else {
                System.out.println("No se encontraron autores vivos en el año " + year);
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida. Por favor ingrese un año válido.");
        }
    }
}