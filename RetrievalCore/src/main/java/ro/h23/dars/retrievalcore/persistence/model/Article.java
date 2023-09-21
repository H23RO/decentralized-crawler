package ro.h23.dars.retrievalcore.persistence.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "siteId", referencedColumnName = "id")
    private Site site;

    @Column(unique = false)
    private String contentsHash;

    @Column(unique = false)
    private String featuredImageHash;

    @NotNull
    @Column(unique=false, length = 2048)
    @Size(max = 2048)
    private String url; // this siteBase + urlPath (?)

    @Column(columnDefinition = "ENUM('NEW', 'VERIFIED')")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ArticleState state;

    @NotNull
    @Column(unique=false, length = 2048)
    @Size(max = 2048)
    private String title;

    private String publishDate;

    private String author;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date extractedDate;

    private Double score;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    //@Column(columnDefinition = "DATETIME")
    protected Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date updateTimestamp;

}
