package ro.h23.dars.webscraper.persistence.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Article {

    @Id
    @Setter(AccessLevel.PRIVATE)
    /*@GeneratedValue(strategy = GenerationType.TABLE, generator="sqlite_article")
    @TableGenerator(name="sqlite_article", table="sqlite_sequence",
            pkColumnName="name", valueColumnName="seq",
            pkColumnValue="Article",
            initialValue=1, allocationSize=1)*/
    @GeneratedValue(strategy=GenerationType.TABLE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer    id;

    @Column(unique=true, length = 2048)
    @Size(max = 2048)
    @NotNull
    private String    url;

    @Column(length = 255)
    @NotNull
    private String templateName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProcessingState state = ProcessingState.WAITING;

    @Size(max = 2000000)
    private String contents = null;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date updateTimestamp;

}