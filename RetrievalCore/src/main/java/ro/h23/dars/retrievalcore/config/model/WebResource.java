package ro.h23.dars.retrievalcore.config.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WebResource {

    byte[] contents;
    String mediaType;

}
