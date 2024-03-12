package hu.bca.library.controllers;

import hu.bca.library.models.Book;
import hu.bca.library.services.BookService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RepositoryRestController("books")
public class BookController {

    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ResponseStatus(HttpStatus.CREATED)

    @RequestMapping("/{bookId}/add_author/{authorId}")
    @ResponseBody Book addAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        return this.bookService.addAuthor(bookId, authorId);
    }

    @RequestMapping("/update_all_with_year")
    @ResponseBody Book updateAll() throws IOException {
        return this.bookService.updateAll();
    }

    @RequestMapping("/query/{country}")
    @ResponseBody Book getBooksByCountry(@PathVariable String country, @RequestParam Optional<Integer> from) {
        if (from.isEmpty())
            return this.bookService.getBooksByCountry(country);
        else {
            return this.bookService.getBooksByCountry(country, from);
        }
    }

}
