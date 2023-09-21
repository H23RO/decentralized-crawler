package ro.h23.dars.retrievalcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import ro.h23.dars.retrievalcore.service.NewPageAddedService;
import ro.h23.dars.retrievalcore.persistence.model.Page;
import ro.h23.dars.retrievalcore.persistence.model.ProcessingState;
import ro.h23.dars.retrievalcore.persistence.repository.PageRepository;

import java.util.List;

@SpringBootApplication
public class OffchainCoreApplication {

	@Autowired
	private NewPageAddedService newPageAddedService;

	@Autowired
	private PageRepository pageRepository;

	private static final Logger logger = LogManager.getLogger(OffchainCoreApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(OffchainCoreApplication.class, args);

	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			logger.info("Starting application");

			// assign all existing pages to scrapers
			while(true) {
				int pageNumber = 0;
				List<Page> pageList = pageRepository.findAllByState(ProcessingState.NEW, Pageable.ofSize(50));
				if (pageList.isEmpty()) {
					break;
				}
 				pageList.forEach(page -> {
					newPageAddedService.process(page);
					page.setState(ProcessingState.IN_PROGRESS);
				});
				pageRepository.saveAllAndFlush(pageList);
			}


		};
	}
}
