package gae.piaz.audit.envers.controller;

import gae.piaz.audit.envers.controller.dto.BookDTO;
import gae.piaz.audit.envers.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public BookDTO createBook(@RequestBody BookDTO book) {
        return bookService.save(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        if (bookService.findById(id).isPresent()) {
            bookService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/editors_history/auditReader")
    public ResponseEntity<?> bookEditorsHistory(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.bookEditorsHistory_auditReader(id));
    }

    @GetMapping("/{id}/editors_history/revisionRepository")
    public ResponseEntity<?> bookEditorsHistory2(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.bookEditorsHistory_revisionRepository(id));
    }
}
