package ro.h23.dars.retrievalcore.persistence.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ro.h23.dars.retrievalcore.auth.persistence.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageUser {

    @EmbeddedId
    private PageUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pageId")
    private Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @Column(columnDefinition = "ENUM('NEW', 'IN_PROGRESS', 'COMPLETE')")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProcessingState state = ProcessingState.NEW;

    private String contentsHash;

    private String featuredImageHash;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date updateTimestamp;

}
