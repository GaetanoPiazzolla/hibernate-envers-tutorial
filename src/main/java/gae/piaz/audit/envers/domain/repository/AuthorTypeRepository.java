package gae.piaz.audit.envers.domain.repository;

import gae.piaz.audit.envers.domain.AuthorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorTypeRepository extends JpaRepository<AuthorType, Integer> {}
