package ro.h23.dars.webcrawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ro.h23.dars.webcrawler.config.CrawlerConfig;
import ro.h23.dars.webcrawler.persistence.model.Site;
import ro.h23.dars.webcrawler.persistence.repository.PageRepository;
import ro.h23.dars.webcrawler.persistence.repository.SiteRepository;
import ro.h23.dars.webcrawler.service.BootstrapService;
import ro.h23.dars.webcrawler.service.CrawlerService;
import ro.h23.dars.webcrawler.service.JsonConverterService;
import ro.h23.dars.webcrawler.service.PageSendToRetrievalCoreService;
import ro.h23.dars.webcrawler.tmp.ScrapperService;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WebCrawlerApplication {

	private static final Logger logger = LogManager.getLogger(WebCrawlerApplication.class);

	@Autowired
	SiteRepository siteRepository;

	@Autowired
	BootstrapService bootstrapService;

	@Autowired
	CrawlerConfig crawlerConfig;

	@Autowired
	PageRepository pageRepository;

	@Autowired
	JsonConverterService jsonConverterService;

	//CrawlerService crawlerService;

	@Autowired
	ScrapperService scrapperService;

	@Autowired
	PageSendToRetrievalCoreService pageSendToRetrievalCoreService;

	public static void main(String[] args) {

		SpringApplication.run(WebCrawlerApplication.class, args);
	}

	/*
	 * There is also a CommandLineRunner method marked as a @Bean, and this runs on
	 * start up. It retrieves all the beans that were created by your application or
	 * that were automatically added by Spring Boot. It sorts them and prints them
	 * out.
	 */
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			logger.info("Starting application");

			logger.info("Obtaining the site list from the retrieval core");
			bootstrapService.start();
			logger.info("Obtaining the site list from the retrieval core... complete");

			logger.info("Starting page send to retrieval core service");
			new Thread(() -> pageSendToRetrievalCoreService.start()).start();
			logger.info("Starting page send to retrieval core service... complete");

			List<Site> siteList = siteRepository.findAll();

			List<Thread> threadList = new ArrayList<>();

			//crawlerService.crawl(site);
			for (final Site site : siteList) {
				logger.info("Starting crawling {}", site.getName());
				//if (site.getName().equals("G4Media")) {
					Thread t = new Thread() {
						@Override
						public void run() {
							CrawlerService crawlerService = new CrawlerService(crawlerConfig, pageRepository, jsonConverterService);
							crawlerService.crawl(site);
						}
					};

					threadList.add(t);
					t.start();
					//break;
				//}
			}
			for(Thread t : threadList) {
				t.join();
			}
			//scrapperService.scrap(site);
			logger.info("Done processing");
		};
	}
}
