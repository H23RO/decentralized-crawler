package ro.h23.dars.retrievalcore.api.publicapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ArticleResponseDto {

    private Long id;
    private String title;
    private String author;
    private Date publishDate;
    private Date extractedDate;
    private Date lastUpdated;
    private String publisher;
    private String url; // this siteBase + urlPath (?)
    private String contentsHash;

}
