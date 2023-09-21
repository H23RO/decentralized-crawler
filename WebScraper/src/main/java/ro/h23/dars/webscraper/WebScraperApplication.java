package ro.h23.dars.webscraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ro.h23.dars.webscraper.exception.ArticleInfoSenderException;
import ro.h23.dars.webscraper.service.ArticleInfoSenderService;
import ro.h23.dars.webscraper.service.ArticleUrlsRetrieverService;
import ro.h23.dars.webscraper.service.ExtractorTemplatesRetrieverService;
import ro.h23.dars.webscraper.service.ScraperManagerService;

@SpringBootApplication
public class WebScraperApplication {

    private static final Logger logger = LogManager.getLogger(WebScraperApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WebScraperApplication.class, args);
    }

    /*
     * There is also a CommandLineRunner method marked as a @Bean, and this runs on
     * start up. It retrieves all the beans that were created by your application or
     * that were automatically added by Spring Boot. It sorts them and prints them
     * out.
     */
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx, ExtractorTemplatesRetrieverService extractorTemplatesRetrieverService, ArticleUrlsRetrieverService articleUrlsRetrieverService, ScraperManagerService scraperManagerService, ArticleInfoSenderService articleInfoSenderService) {
        return args -> {
            logger.info("Starting application");
            try {
                //ExtractorTemplatesDto extractorTemplatesDto = retrievalCoreApiService.retrieveExtractorTemplates();
                //logger.info(extractorTemplatesDto);

                logger.info("Sending article info to RetrievalCore...");
                new Thread(() -> {
                    try {
                        articleInfoSenderService.process();
                    } catch (ArticleInfoSenderException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                logger.info("Sending article info to RetrievalCore. DONE");

                logger.info("Retrieving extractor templates...");
                extractorTemplatesRetrieverService.process();
                logger.info("Retrieving extractor templates. DONE");

                logger.info("Retrieving article URLs...");
                new Thread(() -> { articleUrlsRetrieverService.process(); }).start();
                logger.info("Retrieving article URLs. DONE");

                logger.info("Extracting article info...");
                scraperManagerService.process();
                logger.info("Extracting article info. DONE");


            } catch (Exception e) {
                e.printStackTrace();

            }


        };
    }

}
