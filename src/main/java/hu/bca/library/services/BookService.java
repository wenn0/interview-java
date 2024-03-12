package hu.bca.library.services;

import hu.bca.library.models.Book;

import java.io.IOException;
import java.util.Optional;

public interface BookService {
    Book addAuthor(Long bookId, Long authorId);
    Book updateAll() throws IOException;
    Book getBooksByCountry(String country);
    Book getBooksByCountry(String country, Optional<Integer> from);
}
