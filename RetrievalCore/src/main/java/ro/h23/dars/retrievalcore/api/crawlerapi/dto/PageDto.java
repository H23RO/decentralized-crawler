package ro.h23.dars.retrievalcore.api.crawlerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {

    private String url;

    private Date processedTimestamp;


}
