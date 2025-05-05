package ru.tatarinov.NauJava.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.service.LibraryService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ConsoleUI implements CommandLineRunner {
    private final LibraryService libraryService;
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public ConsoleUI(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Система управления библиотекой ===");
        System.out.println("Введите 'help' для списка команд");

        boolean running = true;
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            Command command = Command.fromString(input);

            if (command == null) {
                System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
                continue;
            }

            switch (command) {
                case LIST -> listBooks();
                case AVAILABLE -> listAvailableBooks();
                case ADD -> addBook();
                case GET -> getBook();
                case UPDATE -> updateBook();
                case DELETE -> deleteBook();
                case BORROW -> borrowBook();
                case RETURN -> returnBook();
                case AUTHOR -> findByAuthor();
                case HELP -> System.out.println(Command.getMenu());
                case EXIT -> running = exitProgram();
            }
        }
    }

    private boolean exitProgram() {
        System.out.println("Завершение работы...");
        return false;
    }

    private void listBooks() {
        System.out.println("Список всех книг:");
        libraryService.getAllBooks().forEach(System.out::println);
    }

    private void listAvailableBooks() {
        System.out.println("Список доступных книг:");
        libraryService.findAvailableBooks().forEach(System.out::println);
    }

    private void addBook() {
        System.out.println("Добавление новой книги:");
        Book book = new Book();

        System.out.print("Название: ");
        book.setTitle(scanner.nextLine());

        System.out.print("Автор: ");
        book.setAuthor(scanner.nextLine());

        System.out.print("ISBN: ");
        book.setIsbn(scanner.nextLine());

        book.setAvailable(true);

        Book savedBook = libraryService.addBook(book);
        System.out.println("Книга добавлена: " + savedBook);
    }

    private void getBook() {
        System.out.print("Введите ID книги: ");
        Long id = Long.parseLong(scanner.nextLine());
        Optional<Book> book = libraryService.getBookById(id);
        if (book.isPresent()) {
            System.out.println("Найдена книга: " + book);
        } else {
            System.out.println("Книга с ID " + id + " не найдена.");
        }
    }

    private void updateBook() {
        System.out.print("Введите ID книги для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());
        Optional<Book> book = libraryService.getBookById(id);

        if (book.isEmpty()) {
            System.out.println("Книга с ID " + id + " не найдена.");
            return;
        }

        System.out.println("Текущие данные: " + book);
        System.out.println("Введите новые данные (оставьте пустым для сохранения текущего значения):");

        System.out.print("Название [" + book.get().getTitle() + "]: ");
        String title = scanner.nextLine();
        if (!title.isEmpty()) {
            book.get().setTitle(title);
        }

        System.out.print("Автор [" + book.get().getAuthor() + "]: ");
        String author = scanner.nextLine();
        if (!author.isEmpty()) {
            book.get().setAuthor(author);
        }

        System.out.print("ISBN [" + book.get().getIsbn() + "]: ");
        String isbn = scanner.nextLine();
        if (!isbn.isEmpty()) {
            book.get().setIsbn(isbn);
        }

        Book updatedBook = libraryService.updateBook(book.orElse(null));
        System.out.println("Книга обновлена: " + updatedBook);
    }

    private void deleteBook() {
        System.out.print("Введите ID книги для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        libraryService.deleteBook(id);
        System.out.println("Книга с ID " + id + " удалена.");
    }

    private void borrowBook() {
        System.out.print("Введите ID книги для взятия: ");
        Long id = Long.parseLong(scanner.nextLine());
        boolean isBorrowBook = libraryService.borrowBook(id);
        if (isBorrowBook) {
            System.out.println("Книга взята.");
        } else {
            System.out.println("Не удалось взять книгу. Возможно, она уже занята или не существует.");
        }
    }

    private void returnBook() {
        System.out.print("Введите ID книги для возврата: ");
        Long id = Long.parseLong(scanner.nextLine());
        boolean isReturnBook = libraryService.returnBook(id);
        if (isReturnBook) {
            System.out.println("Книга возвращена.");
        } else {
            System.out.println("Не удалось вернуть книгу. Возможно, она уже возвращена или не существует.");
        }
    }

    private void findByAuthor() {
        System.out.print("Введите имя автора: ");
        String author = scanner.nextLine();
        List<Book> books = libraryService.findByAuthor(author);
        if (books.isEmpty()) {
            System.out.println("Книги автора " + author + " не найдены.");
        } else {
            System.out.println("Книги автора " + author + ":");
            books.forEach(System.out::println);
        }
    }
}
