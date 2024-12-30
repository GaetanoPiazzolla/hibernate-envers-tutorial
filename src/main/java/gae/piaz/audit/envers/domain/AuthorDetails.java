package gae.piaz.audit.envers.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "author_details")
@Getter
@Setter
@ToString
@Audited
public class AuthorDetails extends AbstractEntity {

    private Boolean isWriter;

    private Boolean isBlogger;

}
