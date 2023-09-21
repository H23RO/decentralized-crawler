package ro.h23.dars.retrievalcore.persistence.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"siteId", "urlPath"})
)
public class Page {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "siteId", referencedColumnName = "id")
    private Site    site;

    @Size(max = 2048)
    @Column(length = 2048)
    @NotNull
    private String    urlPath;

    @Column(columnDefinition = "ENUM('NEW', 'IN_PROGRESS', 'COMPLETE')")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProcessingState state = ProcessingState.NEW;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date updateTimestamp;

    /*
    @OneToMany(
        mappedBy = "tag",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Userxxx> users = new ArrayList<>();
     */

}
