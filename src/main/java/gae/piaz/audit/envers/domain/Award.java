package gae.piaz.audit.envers.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "authors")
@Getter
@Setter
@ToString
@Audited
public class Award extends AbstractEntity {

    private String name;

    // one-to-many one-directional relationship
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
}
