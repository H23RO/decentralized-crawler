package ro.h23.dars.retrievalcore.config.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SiteInfo {

    private String name;

    private String urlBase;

    private String logoUrl;

    private PageTypeClassifier pageTypeClassifier;

    private ExtractorTemplate extractorTemplate;
}
