package gae.piaz.audit.envers.controller.dto;


import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record HistoryDTO(
        List<HistoryChangesDTO> changes,
        String modifiedBy,
        LocalDateTime modifiedAt) {

    public record HistoryChangesDTO(
            HistoryRevisionType type,
            String field,
            String oldValue,
            String newValue) {}

    public enum HistoryRevisionType {
        UPDATE,
        DELETE,
        INSERT,
        INSERT_RELATED,
        DELETE_RELATED
    }
}
