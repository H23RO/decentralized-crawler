/*
 * Copyright (C) 2017 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details.
 */
package ro.h23.dars.retrievalcore.persistence.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ro.h23.dars.retrievalcore.auth.persistence.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Site {

    @Id
    @Setter(AccessLevel.PRIVATE)
    /*@SequenceGenerator(name = "site_seq",
            sequenceName = "site_sequence",
            initialValue = 1, allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_seq")*/
    //@GeneratedValue(strategy= GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(unique = true)
    @Size(max = 100)
    @NotBlank
    @NotNull
    private String name;

    @Column(unique = true)
    @Size(max = 100)
    @NotBlank
    @NotNull
    private String urlBase;

    @Size(max = 512)
    @NotBlank
    private String logoUrl;

    @NotBlank
    @NotNull
    @Size(max = 2048)
    private String pageTypeClassifier;

    @NotBlank
    @NotNull
    //@Size(max = 2048)
    @Lob
    @Basic(fetch=FetchType.LAZY)
    private String extractorTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    private Date updateTimestamp;
}
