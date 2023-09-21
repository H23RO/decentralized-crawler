package ro.h23.dars.webcrawler.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component("crawlerConfig")
@ConfigurationProperties(prefix="crawler")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CrawlerConfig {

    private int siteWaitTimeMin;
    private int siteWaitTimeMax;
    private String geckoDriverFilename;

}
