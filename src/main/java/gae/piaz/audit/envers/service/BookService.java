package gae.piaz.audit.envers.service;

import gae.piaz.audit.envers.controller.dto.BookDTO;
import gae.piaz.audit.envers.domain.Book;
import gae.piaz.audit.envers.domain.audit.CustomRevisionEntity;
import gae.piaz.audit.envers.domain.repository.AuthorRepository;
import gae.piaz.audit.envers.domain.repository.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Book> findById(Integer id) {
        return bookRepository.findById(id);
    }

    public BookDTO save(BookDTO bookDTO) {
        Book book = new Book();
        book.setAuthor(authorRepository.findById(bookDTO.authorId()).orElseThrow());
        book.setTitle(bookDTO.title());
        book.setDescription(bookDTO.description());
        bookRepository.save(book);
        return new BookDTO(book.getAuthor().getId(), book.getTitle(), book.getDescription());
    }

    public List<UUID> bookEditorsHistory_auditReader(Integer id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List revisions = auditReader
                .createQuery()
                .forRevisionsOfEntity(Book.class, false, true)
                .add(AuditEntity.id().eq(id))
                .addProjection(AuditEntity.revisionProperty("authorUuid"))
                .getResultList();
        return (List<UUID>) revisions;
    }

    public List<UUID> bookEditorsHistory_revisionRepository(Integer id) {
        Revisions<Integer, Book> revisions = bookRepository.findRevisions(id);
        return revisions.stream()
                .map(integerBookRevision -> {
                    return getCustomRevisionEntity(integerBookRevision).getAuthorUuid();
                })
                .collect(Collectors.toList());
    }

    public CustomRevisionEntity getCustomRevisionEntity(Revision<Integer, Book> revision) {
        RevisionMetadata<Integer> metadata = revision.getMetadata();
        Object delegate = metadata.getDelegate();
        if (delegate instanceof CustomRevisionEntity revisionEntity) {
            return revisionEntity;
        }
        return new CustomRevisionEntity();
    }

    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }
}
