package gae.piaz.audit.envers.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@ToString
@Audited
public class Tag extends AbstractEntity {

    private String tagName;

    @ManyToMany(mappedBy = "tags")
    private Set<Author> authors = new HashSet<>();

}
