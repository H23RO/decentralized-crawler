package ro.h23.dars.webscraper.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExtractorTemplateInfo {

    private String templateName;

    private String[] removeElements;
    private String[] title;
    private String[] contents;
    private String[] featuredImage;
    private String[] media;
    private String[] publishDate;
    private String[] author;

}
