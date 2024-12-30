package gae.piaz.audit.envers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gae.piaz.audit.envers.controller.dto.AuthorAddressDTO;
import gae.piaz.audit.envers.controller.dto.AuthorDTO;
import gae.piaz.audit.envers.controller.dto.AuthorDetailsDTO;
import gae.piaz.audit.envers.controller.dto.BookDTO;
import gae.piaz.audit.envers.domain.Author;
import gae.piaz.audit.envers.domain.AuthorType;
import gae.piaz.audit.envers.domain.Book;
import gae.piaz.audit.envers.domain.Tag;
import gae.piaz.audit.envers.domain.repository.AuthorRepository;
import gae.piaz.audit.envers.domain.repository.BookRepository;
import java.util.List;
import java.util.Optional;

import gae.piaz.audit.envers.domain.repository.TagRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EnversTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private AuthorRepository authorRepository;

    @Autowired private TagRepository tagRepository;

    @Autowired private BookRepository bookRepository;

    @Test
    void createAuthor_createsNewAuthorRevision() throws Exception {
        AuthorDTO author = new AuthorDTO(null, 1, "Gigino", "Fiorino", null);

        String responseString =
                mockMvc.perform(
                                post("/api/authors")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(author)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        AuthorDTO response = objectMapper.readValue(responseString, AuthorDTO.class);
        Page<Revision<Integer, Author>> authorRevisions = getRevisions(response.authorId());
        Assertions.assertEquals(1, authorRevisions.getTotalElements());
    }

    @Test
    void updateAuthor_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        AuthorDTO authorDTO = new AuthorDTO(author.getId(), 1, "Gaetano", "Piazzellelle", null);

        mockMvc.perform(
                        put("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authorDTO)))
                .andExpect(status().isOk());

        authorRevisions = getRevisions(author);
        Assertions.assertEquals(2, authorRevisions.getTotalElements());
    }

    @Test
    void insertBook_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        BookDTO book = new BookDTO(author.getId(), "New article", "A new shiny article By Gaetano");
        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk());

        authorRevisions = getRevisions(author);
        Assertions.assertEquals(2, authorRevisions.getTotalElements());
    }

    @Test
    void deleteBook_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();
        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        Book book = createNewBook(author);
        authorRevisions = getRevisions(author);
        Assertions.assertEquals(2, authorRevisions.getTotalElements());

        mockMvc.perform(delete("/api/books/" + book.getId())).andExpect(status().isNoContent());

        authorRevisions = authorRepository.findRevisions(author.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(3, authorRevisions.getTotalElements());
    }

    private Page<Revision<Integer, Author>> getRevisions(Author author) {
        return this.getRevisions(author.getId());
    }

    private Page<Revision<Integer, Author>> getRevisions(Integer authorId) {
        return authorRepository.findRevisions(authorId, Pageable.ofSize(100));
    }

    private Book createNewBook(Author author) {
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(Instancio.of(String.class).create());
        book.setDescription(Instancio.of(String.class).create());
        book = bookRepository.save(book);
        return book;
    }

    @Test
    void addAward_noNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        mockMvc.perform(
                        post("/api/authors/" + author.getId() + "/award")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("Best author"))
                .andExpect(status().isOk());

        authorRevisions = authorRepository.findRevisions(author.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(1, authorRevisions.getTotalElements());
    }

    @Test
    void addAlias_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        mockMvc.perform(
                        put("/api/authors/" + author.getId() + "/alias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("Marty"))
                .andExpect(status().isOk());

        authorRevisions = authorRepository.findRevisions(author.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(2, authorRevisions.getTotalElements());
    }

    @Test
    void updateAuthorType_createdNewAuthorRevision_evenIfAuthorTypeIsNotAudited() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);

        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        AuthorDTO authorDTO = new AuthorDTO(author.getId(), 2, null, null, null);

        mockMvc.perform(
                        put("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authorDTO)))
                .andExpect(status().isOk());

        authorRevisions = authorRepository.findRevisions(author.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(2, authorRevisions.getTotalElements());

        Optional<Revision<Integer, Author>> newType =
                authorRevisions.stream()
                        .filter(revision -> revision.getEntity().getAuthorType().getId().equals(2))
                        .findFirst();
        Assertions.assertTrue(newType.isPresent());
    }

    @Test
    void addReader_createsNewAuthorRevisionWithFlag() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        mockMvc.perform(post("/api/authors/" + author.getId() + "/add_reader"))
                .andExpect(status().isOk());

        authorRevisions = authorRepository.findRevisions(author.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(2, authorRevisions.getTotalElements());

        String revisions =
                mockMvc.perform(get("/api/authors/" + author.getId() + "/readers_history"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        List<?> readers = objectMapper.readValue(revisions, List.class);
        Assertions.assertEquals(2, readers.size());
    }

    @Test
    void deleteAuthor_createsNewAuthorRevision_withDeletedType() throws Exception {
        Author author = createAndSaveAuthor();

        mockMvc.perform(delete("/api/authors/" + author.getId())).andExpect(status().isOk());

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(2, authorRevisions.getTotalElements());

        Optional<Revision<Integer, Author>> deletedRevision =
                authorRevisions.stream()
                        .filter(
                                revision ->
                                        revision.getMetadata()
                                                .getRevisionType()
                                                .equals(RevisionMetadata.RevisionType.DELETE))
                        .findFirst();
        Assertions.assertTrue(deletedRevision.isPresent());

        Optional<Author> deletedAuthor = authorRepository.findById(author.getId());
        Assertions.assertTrue(deletedAuthor.isPresent());
        Assertions.assertTrue(deletedAuthor.get().isDeleted());
    }

    @Test
    void editBook_containsCustomRevisionInfo_usingAuditReader() throws Exception {
        Author author = createAndSaveAuthor();

        // Create a book
        Book book = createNewBook(author);

        // Update the book
        book.setTitle("New title");
        book = bookRepository.save(book);

        String response = mockMvc.perform(get("/api/books/" + book.getId() + "/editors_history/auditReader"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        List<?> authors = objectMapper.readValue(response, List.class);
        Assertions.assertEquals(2, authors.size());

    }

    @Test
    void editBook_containsCustomRevisionInfo_usingRevisionRepo() throws Exception {
        Author author = createAndSaveAuthor();

        // Create a book
        Book book = createNewBook(author);

        // Update the book
        book.setTitle("New title");
        book = bookRepository.save(book);

        String response = mockMvc.perform(get("/api/books/" + book.getId() + "/editors_history/revisionRepository"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        List<?> authors = objectMapper.readValue(response, List.class);
        Assertions.assertEquals(2, authors.size());

    }

    @Test
    void editAuthor_updateDetails_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        AuthorDetailsDTO detailsDTO = new AuthorDetailsDTO(
                true,
                false);

        mockMvc.perform(
                        put("/api/authors/" + author.getId() + "/details")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(detailsDTO)))
                .andExpect(status().isOk());

        authorRevisions = authorRepository.findRevisions(author.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(2, authorRevisions.getTotalElements());
    }

    @Test
    void editAuthor_updateAdress_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        AuthorAddressDTO address = new AuthorAddressDTO(
                "Via Roma",
                "Roma");

        mockMvc.perform(
                        put("/api/authors/" + author.getId() + "/address")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isOk());

        authorRevisions = authorRepository.findRevisions(author.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(2, authorRevisions.getTotalElements());
    }

    @Test
    void editAuthor_addTag_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();
        Tag tag = createAndSaveTag();

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(1, authorRevisions.getTotalElements());

        Page<Revision<Integer, Tag>> tagRevisions = tagRepository.findRevisions(tag.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(1, tagRevisions.getTotalElements());

        mockMvc.perform(
                        post("/api/authors/" + author.getId() + "/tag/"+ tag.getId()))
                .andExpect(status().isOk());

        author = authorRepository.findById(author.getId()).get();
        Assertions.assertEquals(1, author.getTags().size());

        authorRevisions = getRevisions(author);

        tagRevisions = tagRepository.findRevisions(tag.getId(), Pageable.ofSize(10));

        Assertions.assertEquals(2, tagRevisions.getTotalElements());
        Assertions.assertEquals(2, authorRevisions.getTotalElements());
    }

    @Test
    void editAuthor_removeTag_createsNewAuthorRevision() throws Exception {
        Author author = createAndSaveAuthor();
        Tag tag = createAndSaveTag();
        author.getTags().add(tag);
        author = authorRepository.save(author);

        Page<Revision<Integer, Author>> authorRevisions = getRevisions(author);
        Assertions.assertEquals(2, authorRevisions.getTotalElements());

        Page<Revision<Integer, Tag>> tagRevisions = tagRepository.findRevisions(tag.getId(), Pageable.ofSize(10));
        Assertions.assertEquals(2, tagRevisions.getTotalElements());

        mockMvc.perform(
                        delete("/api/authors/" + author.getId() + "/tag/"+ tag.getId()))
                .andExpect(status().isOk());

        author = authorRepository.findById(author.getId()).get();
        Assertions.assertEquals(0, author.getTags().size());

        authorRevisions = getRevisions(author);

        tagRevisions = tagRepository.findRevisions(tag.getId(), Pageable.ofSize(10));

        Assertions.assertEquals(3, tagRevisions.getTotalElements());
        Assertions.assertEquals(3, authorRevisions.getTotalElements());
    }

    private Tag createAndSaveTag() {
        Tag tag = new Tag();
        tag.setTagName(Instancio.of(String.class).create());
        tag = tagRepository.save(tag);
        return tag;
    }

    private Author createAndSaveAuthor() {
        Author author = new Author();
        author.setFirstName(Instancio.of(String.class).create());
        author.setLastName(Instancio.of(String.class).create());
        author.setAliases(Instancio.createSet(String.class));
        author.setAuthorType(new AuthorType());
        author.getAuthorType().setId(1);
        author.getAuthorType().setDatabaseVersion(0);
        author.setNumberOfReaders(Instancio.of(Integer.class).create());
        author.setDatabaseVersion(0);
        author = authorRepository.save(author);
        return author;
    }
}
