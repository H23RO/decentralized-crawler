package ro.h23.dars.retrievalcore.api.crawlerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SitePagesDto {

    private String siteName;

    private List<String> pages;

    private Date creationTimestamp;

}