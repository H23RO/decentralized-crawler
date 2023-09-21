package ro.h23.dars.webscraper.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component("scraperConfig")
@ConfigurationProperties(prefix="scraper")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ScraperConfig {

    private int siteWaitTimeMin;
    private int siteWaitTimeMax;
    private String geckoDriverFilename;
    private String outputDir;

}
