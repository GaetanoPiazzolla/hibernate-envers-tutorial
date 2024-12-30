package gae.piaz.audit.envers.service;

import gae.piaz.audit.envers.controller.dto.AuthorAddressDTO;
import gae.piaz.audit.envers.controller.dto.AuthorDTO;
import gae.piaz.audit.envers.controller.dto.AuthorDetailsDTO;
import gae.piaz.audit.envers.domain.Address;
import gae.piaz.audit.envers.domain.Author;
import gae.piaz.audit.envers.domain.AuthorDetails;
import gae.piaz.audit.envers.domain.Award;
import gae.piaz.audit.envers.domain.Tag;
import gae.piaz.audit.envers.domain.repository.AuthorRepository;
import gae.piaz.audit.envers.domain.repository.AuthorTypeRepository;
import gae.piaz.audit.envers.domain.repository.AwardRepository;
import gae.piaz.audit.envers.domain.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

import lombok.AllArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AwardRepository awardRepository;
    private final AuthorTypeRepository authorTypeRepository;
    private final TagRepository tagRepository;

    @PersistenceContext private EntityManager entityManager;

    public AuthorDTO update(AuthorDTO author) {
        Author authorEntity =
                authorRepository
                        .findById(author.authorId())
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        authorEntity.setFirstName(author.firstName());
        authorEntity.setLastName(author.lastName());
        authorEntity.setAliases(author.aliases());

        if (author.authorTypeId() != null) {
            authorEntity.setAuthorType(
                    authorTypeRepository.findById(author.authorTypeId()).orElseThrow());
        }

        authorEntity = authorRepository.save(authorEntity);
        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    public AuthorDTO addAlias(Integer id, String alias) {
        Author authorEntity =
                authorRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        authorEntity.getAliases().add(alias);
        authorEntity = authorRepository.save(authorEntity);
        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    public AuthorDTO save(AuthorDTO author) {
        Author authorEntity = new Author();
        authorEntity.setFirstName(author.firstName());
        authorEntity.setLastName(author.lastName());
        authorEntity.setAliases(author.aliases());
        authorEntity.setAuthorType(
                authorTypeRepository.findById(author.authorTypeId()).orElseThrow());
        authorEntity.setNumberOfReaders(0);
        authorEntity = authorRepository.save(authorEntity);
        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    public AuthorDTO saveAward(Integer id, String award) {
        Author authorEntity =
                authorRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        Award awardEntity = new Award();
        awardEntity.setName(award);
        awardEntity.setAuthor(authorEntity);
        awardRepository.save(awardEntity);

        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    public AuthorDTO addReader(Integer id) {
        Author authorEntity =
                authorRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        authorEntity.setNumberOfReaders(authorEntity.getNumberOfReaders() + 1);
        authorEntity = authorRepository.save(authorEntity);
        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    // Returns the List of revision number where the numberOfReaders property has changed
    public List<Integer> readersHistory(Integer id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return (List<Integer>) auditReader
                .createQuery()
                .forRevisionsOfEntity(Author.class, false, true)
                .add(AuditEntity.id().eq(id))
                .add(AuditEntity.property("numberOfReaders").hasChanged())
                .addProjection(AuditEntity.revisionNumber())
                .getResultList();
    }


    public void deleteAuthor(Integer id) {
        authorRepository.deleteById(id);
    }

    public AuthorDTO updateDetails(Integer id, AuthorDetailsDTO authorDetailsDTO) {
        Author authorEntity =
                authorRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        if(authorEntity.getAuthorDetails() == null) {
            authorEntity.setAuthorDetails(new AuthorDetails());
        }

        authorEntity.getAuthorDetails().setIsBlogger(authorDetailsDTO.isBlogger());
        authorEntity.getAuthorDetails().setIsWriter(authorDetailsDTO.isWriter());
        authorEntity = authorRepository.save(authorEntity);

        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    public AuthorDTO updateAddress(Integer id, AuthorAddressDTO address) {
        Author authorEntity =
                authorRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        if(authorEntity.getAddress() == null) {
            authorEntity.setAddress(new Address());
        }

        authorEntity.getAddress().setCity(address.city());
        authorEntity.getAddress().setStreet(address.street());

        authorEntity = authorRepository.save(authorEntity);

        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    public AuthorDTO addTag(Integer authorId, Integer tagId) {

        Author authorEntity =
                authorRepository
                        .findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        Tag tag = tagRepository.findById(tagId).orElseThrow();

        authorEntity.getTags().add(tag);
        authorEntity = authorRepository.save(authorEntity);

        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }

    public AuthorDTO removeTag(Integer authorId, Integer tagId) {
        Author authorEntity =
                authorRepository
                        .findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        Tag tag = tagRepository.findById(tagId).orElseThrow();

        authorEntity.getTags().remove(tag);
        authorEntity = authorRepository.save(authorEntity);

        return new AuthorDTO(
                authorEntity.getId(),
                authorEntity.getAuthorType().getId(),
                authorEntity.getFirstName(),
                authorEntity.getLastName(),
                authorEntity.getAliases());
    }
}
