package gae.piaz.audit.envers.domain.repository;

import gae.piaz.audit.envers.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>, RevisionRepository<Tag, Integer, Integer> {
}
