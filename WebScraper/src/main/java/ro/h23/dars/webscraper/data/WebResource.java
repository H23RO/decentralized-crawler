package ro.h23.dars.webscraper.data;

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
