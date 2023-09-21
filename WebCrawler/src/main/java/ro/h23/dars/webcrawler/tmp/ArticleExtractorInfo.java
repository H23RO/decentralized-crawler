package ro.h23.dars.webcrawler.tmp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ArticleExtractorInfo {

    private String[] removeElements;
    private String[] title;
    private String[] contents;
    private String[] featuredImage;
    private String[] media;
    private String[] publishDate;
    private String[] author;

}
