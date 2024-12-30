package gae.piaz.audit.envers.domain.repository;

import gae.piaz.audit.envers.domain.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardRepository
        extends JpaRepository<Award, Integer>, RevisionRepository<Award, Integer, Integer> {}
