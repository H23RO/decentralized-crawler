package ro.h23.dars.webcrawler.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SiteDto {

    private String name;

    private String urlBase;

    private String pageTypeClassifier;

}
