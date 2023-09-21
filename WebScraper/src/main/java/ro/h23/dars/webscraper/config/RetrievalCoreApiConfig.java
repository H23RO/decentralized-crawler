package ro.h23.dars.webscraper.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("retrievalCoreApiConfig")
@ConfigurationProperties(prefix="retrievalcore-api")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RetrievalCoreApiConfig {

    private String server;
    private String authenticationPath;
    private String username;
    private String password;

}
