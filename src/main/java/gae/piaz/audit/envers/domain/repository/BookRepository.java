package gae.piaz.audit.envers.domain.repository;

import gae.piaz.audit.envers.domain.Author;
import gae.piaz.audit.envers.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>,
        RevisionRepository<Book, Integer, Integer> {}
