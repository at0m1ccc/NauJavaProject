package ru.tatarinov.NauJava.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tatarinov.NauJava.repository.BookRepository;

@Controller
@RequestMapping("/ui/books")
public class BookWebController {
    private final BookRepository bookRepository;

    @Autowired
    public BookWebController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String getAllBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "books";
    }
}
