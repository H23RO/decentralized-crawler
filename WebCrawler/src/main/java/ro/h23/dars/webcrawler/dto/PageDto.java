package ro.h23.dars.webcrawler.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageDto {

    private String url;

    private Date processedTimestamp;

}
