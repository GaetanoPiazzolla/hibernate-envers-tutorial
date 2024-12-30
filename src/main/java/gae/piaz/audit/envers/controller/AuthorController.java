package gae.piaz.audit.envers.controller;

import gae.piaz.audit.envers.controller.dto.AuthorAddressDTO;
import gae.piaz.audit.envers.controller.dto.AuthorDTO;
import gae.piaz.audit.envers.controller.dto.AuthorDetailsDTO;
import gae.piaz.audit.envers.service.AuthorService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PutMapping
    public AuthorDTO updateAuthor(@RequestBody AuthorDTO author) {
        return authorService.update(author);
    }

    @PutMapping("/{id}/alias")
    public AuthorDTO addAlias(@PathVariable Integer id, @RequestBody String alias) {
        return authorService.addAlias(id, alias);
    }

    @PutMapping("/{id}/details")
    public AuthorDTO updateDetails(@PathVariable Integer id, @RequestBody AuthorDetailsDTO authorDetailsDTO) {
        return authorService.updateDetails(id, authorDetailsDTO);
    }

    @PutMapping("/{id}/address")
    public AuthorDTO updateAddress(@PathVariable Integer id, @RequestBody AuthorAddressDTO address) {
        return authorService.updateAddress(id, address);
    }

    @PostMapping("/{authorId}/tag/{tagId}")
    public AuthorDTO addTag(@PathVariable Integer authorId, @PathVariable Integer tagId) {
        return authorService.addTag(authorId, tagId);
    }

    @DeleteMapping("/{authorId}/tag/{tagId}")
    public AuthorDTO removeTag(@PathVariable Integer authorId, @PathVariable Integer tagId) {
        return authorService.removeTag(authorId, tagId);
    }

    @PostMapping
    public AuthorDTO saveAuthor(@RequestBody AuthorDTO author) {
        return authorService.save(author);
    }

    @PostMapping("/{id}/award")
    public AuthorDTO saveAward(@PathVariable Integer id, @RequestBody String award) {
        return authorService.saveAward(id, award);
    }

    @PostMapping("/{id}/add_reader")
    public AuthorDTO addReader(@PathVariable Integer id) {
        return authorService.addReader(id);
    }

    @GetMapping("/{id}/readers_history")
    public List<Integer> readersHistory(@PathVariable Integer id) {
        return authorService.readersHistory(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAuthor(@PathVariable Integer id) {
        authorService.deleteAuthor(id);
    }
}
