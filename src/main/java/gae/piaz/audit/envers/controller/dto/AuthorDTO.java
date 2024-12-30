package gae.piaz.audit.envers.controller.dto;

import java.util.Set;

public record AuthorDTO(
        Integer authorId,
        Integer authorTypeId,
        String firstName,
        String lastName,
        Set<String> aliases) {}
