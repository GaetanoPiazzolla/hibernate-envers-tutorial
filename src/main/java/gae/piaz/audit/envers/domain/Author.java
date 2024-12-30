package gae.piaz.audit.envers.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "authors")
@Getter
@Setter
@ToString
@Audited
@SQLDelete(sql = "UPDATE authors SET deleted = true WHERE id = ? AND database_version = ?")
// @SQLRestriction("deleted = false")
public class Author extends AbstractEntity {

    private String firstName;

    private String lastName;

    // 1- one-to-many bidirectional relationship (creates revision for both sides).
    @OneToMany(mappedBy = "author", fetch = jakarta.persistence.FetchType.LAZY)
    private Set<Book> books;

    // 2- one-to-many one-directional relationship (only present in Many side)
    // Creates revision only for the Many side.
    // @OneToMany(fetch = jakarta.persistence.FetchType.LAZY)
    // private Set<Award> awards;ù

    // 1 and 2 applies also to self referencing relationships.

    // 3 collection of basic types.
    // Creates revision if we add or remove an element from the collection.
    @ElementCollection
    @CollectionTable(name = "author_aliases", joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "alias")
    private Set<String> aliases;

    // 4- many-to-one one-directional relationship
    // with @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    // AuthorType is not audited, but any update on Author will create a revision that points to the
    // NOT audited AuthorType
    // This is useful for dictionary-like entities, which don't change and don't need to be audited.
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "author_type_id")
    @Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED)
    private AuthorType authorType;

    // 5- Tracking specific changes with the Modified-Flag.
    // A modification flag is stored for each property in the annotated class or for the annotated
    // property.
    // The flag stores information if a property has been changed at a given revision.
    // This can be used in queries.
    @Audited(withModifiedFlag = true)
    @Column(name = "number_of_readers", nullable = false, columnDefinition = "integer DEFAULT 0")
    private Integer numberOfReaders;

    // 6- Soft delete
    // when handling soft deletion, we need to add a new column to the table to store the deleted
    // status.
    // in order to track the deletion of an entity with Envers, we need to still trigger DELETE on
    // the entity, not UPDATE.
    // We can do this by using the @SQLDelete annotation (and @SQLRestriction to filter out the
    // deleted entities).
    @Column(name = "deleted")
    private boolean deleted;

    // 7- Best Practices for @Version fields
    // The @Version field is used to implement optimistic locking.
    // With auditing and envers: Include @Version Only if Necessary.
    // in this case, we remove it.

    // 8- Enhancing the REVINFO table with custom columns
    // check gae.piaz.audit.envers.domain.audit.CustomRevisionEntity

    // 9- Embedded columns and OneToOne relationships.
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL)
    @JoinColumn(name = "author_details_id")
    private AuthorDetails authorDetails;

    @Embedded
    private Address address;

    // 10- Many-to-Many bidirectional relationships
    // linking or unlinking a TAG to an author will create a new revision for the author and the tag,
    // ONLY if we save the author, which is the owning side of the relationship.
    // If we save the TAG, no revision will be created for the author.
    @JoinTable(
        name = "author_tags",
        joinColumns = @JoinColumn(name = "author_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tag> tags = new HashSet<>();

    // 11- TODO building a DIFF history

}
