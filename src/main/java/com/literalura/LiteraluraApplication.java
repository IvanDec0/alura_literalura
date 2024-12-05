package com.literalura;


import com.literalura.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.Scanner;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	private final Scanner scanner;
	private final BookService bookService;

	public LiteraluraApplication(BookService bookService) {
		this.bookService = bookService;
		this.scanner = new Scanner(System.in);
	}

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Mostrar el menú de inicio
		showMenu();
	}

	// Método para mostrar el menú de opciones al usuario
	private void showMenu() {
		boolean running = true;

		while (running) {
			System.out.println("----- MENÚ -----");
			System.out.println("1. Buscar libro por título");
			System.out.println("2. Buscar libro por ID");
			System.out.println("3. Ver todos los libros");
			System.out.println("4. Filtrar libros por idioma");
			System.out.println("5. Ver todos los autores");
			System.out.println("6. Ver autores vivos en un determinado año");
			System.out.println("7. Mostrar cantidad de libros por idioma");
			System.out.println("8. Salir");
			System.out.print("Seleccione una opción: ");
			int option = scanner.nextInt();
			scanner.nextLine(); // Consumir la nueva línea

			switch (option) {
				case 1:
					// Buscar libro por título
					bookService.searchBookByTitle();
					break;
				case 2:
					// Buscar libro por ID
					bookService.showBookDetails();
					break;
				case 3:
					// Ver todos los libros
					bookService.showAllBooks();
					break;
				case 4:
					// Filtrar libros por idioma
					bookService.filterBooksByLanguage();
					break;
				case 5:
					// Ver todos los autores
					bookService.showAllAuthors();
					break;
				case 6:
					// Ver autores vivos en un determinado año
					bookService.showAuthorsAliveInYear();
					break;
				case 7:
					// Mostrar cantidad de libros por idioma
					bookService.showBooksCountByLanguage();
					break;
				case 8:
					// Salir
					System.out.println("Saliendo...");
					running = false;
					scanner.close();
					break;
				default:
					System.out.println("Opción no válida. Por favor, intente nuevamente.");
					break;
			}
		}
		System.exit(0); // Finaliza la aplicación
	}
}