package gae.piaz.audit.envers.domain.audit;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import java.util.UUID;

@Entity
@Table(name = "revinfo_custom")
@RevisionEntity(CustomRevisionListener.class)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "rev"))
@AttributeOverride(name = "timestamp", column = @Column(name = "revtstmp"))
public class CustomRevisionEntity extends DefaultRevisionEntity {

    @Column(name = "author_uuid")
    private UUID authorUuid;
}
