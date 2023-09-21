package ro.h23.dars.retrievalcore.api.crawlerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteDto {

    private String name;

    private String urlBase;

    private String pageTypeClassifier;

}
