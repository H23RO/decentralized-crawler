package ro.h23.dars.webscraper.data;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ArticleInfo {
    String url;
    String title;
    String contents;
    String contentsTextOnly;
    String contentsTextOnlyHash;
    String featuredImageUrl;
    String featuredImageHash;
    String publishDate;
    String author;
    Date extractedDate;
}
