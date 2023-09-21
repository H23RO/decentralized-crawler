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

    public enum RecrawlType {
        INTERVAL, TIME
    }

    private int siteWaitTimeMin;
    private int siteWaitTimeMax;
    private int crawlerCount;
    private RecrawlType recrawlType;
    private Duration recrawlInterval;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime recrawlTime;

    private String geckoDriverFilename;

}
