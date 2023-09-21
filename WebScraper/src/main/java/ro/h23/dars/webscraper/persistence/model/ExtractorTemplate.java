package ro.h23.dars.webscraper.persistence.model;

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
@ToString
@NoArgsConstructor
public class ExtractorTemplate {

    @Id
    @Setter(AccessLevel.PRIVATE)
    /*@GeneratedValue(strategy = GenerationType.TABLE, generator="sqlite_extractortemplate")
    @TableGenerator(name="sqlite_extractortemplate", table="sqlite_sequence",
           pkColumnName="name", valueColumnName="seq",
           pkColumnValue="ExtractorTemplate",
            initialValue=1, allocationSize=1)*/
    @GeneratedValue(strategy=GenerationType.TABLE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer    id;

    @Column(unique=true, length = 2048)
    @Size(max = 2048)
    @NotNull
    private String    name;

    @Column(length = 2048)
    @NotNull
    private String template;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date updateTimestamp;

}