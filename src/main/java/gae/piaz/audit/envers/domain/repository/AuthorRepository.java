package gae.piaz.audit.envers.domain.repository;

import gae.piaz.audit.envers.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository
        extends JpaRepository<Author, Integer>, RevisionRepository<Author, Integer, Integer> {}
