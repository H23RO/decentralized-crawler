package ro.h23.dars.webcrawler.persistence.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"urlPath", "siteId"})
)
public class Page {

    @Id
    @Setter(AccessLevel.PROTECTED)
    @GeneratedValue(strategy= GenerationType.AUTO)
    /*@SequenceGenerator(name = "page_seq",
            sequenceName = "page_sequence",
            initialValue = 1, allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_seq")*/
    private Integer    id;

    @OneToOne
    @JoinColumn(name = "siteId", referencedColumnName = "id")
    private Site    site;

    @Size(max = 2048)
    @Column(length = 2048)
    @NotNull
    private String    urlPath;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PageState state = PageState.WAITING;

    @Size(max = 2048)
    private String details = null;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PageType pageType = PageType.UNKNOWN;

    private Integer numberOfLinks = 0;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date updateTimestamp;



}