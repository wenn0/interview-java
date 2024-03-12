package hu.bca.library.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bca.library.models.Author;
import hu.bca.library.models.Book;
import hu.bca.library.repositories.AuthorRepository;
import hu.bca.library.repositories.BookRepository;
import hu.bca.library.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public Book addAuthor(Long bookId, Long authorId) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Book with id %s not found", bookId));
        }
        Optional<Author> author = this.authorRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Author with id %s not found", authorId));
        }

        List<Author> authors = book.get().getAuthors();
        authors.add(author.get());

        book.get().setAuthors(authors);
        return this.bookRepository.save(book.get());
    }

    @Override
    public Book updateAll() {
        Iterable<Book> books = this.bookRepository.findAll();
        books.forEach(book -> {
            try {
                book.setYear(Integer.parseInt(yearParser(book.getWorkId())));
                this.bookRepository.save(book);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Year not found for workId %s", book.getWorkId()));
            }
        });
        return null;
    }

    @Override
    public Book getBooksByCountry(String country) {
        Iterable<Author> authors = this.authorRepository.findAll();

        authors.forEach(author -> {
            if (author.getCountry().equals(country)) {
                author.getBooks().forEach(book -> System.out.println(book.getTitle()));
            }
        });

        return null;
    }

    @Override
    public Book getBooksByCountry(String country, Optional<Integer> year) {
        Iterable<Author> authors = this.authorRepository.findAll();

        List<Book> resultList = new ArrayList<>();

        authors.forEach(author -> {
            if (author.getCountry().equals(country)) {
                author.getBooks().forEach(book -> {
                    if (year.isPresent() && book.getYear() >= (year.get())) {
                        resultList.add(book);
                    }
                });
            }
        });

        List<Book> sorted = resultList.stream().sorted(Comparator.comparing(Book::getYear)).toList();

        sorted.forEach(x -> {
            System.out.println(x.getTitle() + ", " + String.format("Publish Date: %s", x.getYear()));
        });

        return null;
    }

    private static JsonNode getJsonData(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(url);
    }

    private String yearParser(String workId) throws IOException {

        String year = "0";
        URL url = new URL(String.format("https://openlibrary.org/works/%s.json", workId));
        if (getJsonData(url).get("first_publish_date") != null) {
            Matcher m = YEAR_PATTERN.matcher(getJsonData(url).get("first_publish_date").asText());
            while (m.find()) {
                year = m.group();
            }
        }
        return year;
    }
}
