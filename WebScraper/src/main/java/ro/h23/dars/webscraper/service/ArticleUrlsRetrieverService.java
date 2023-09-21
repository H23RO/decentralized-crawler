package ro.h23.dars.webscraper.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.dto.ArticleUrlsDto;
import ro.h23.dars.webscraper.persistence.model.Article;
import ro.h23.dars.webscraper.persistence.model.ProcessingState;
import ro.h23.dars.webscraper.persistence.repository.ArticleRepository;

@Service
public class ArticleUrlsRetrieverService {
    private static final Logger logger = LogManager.getLogger(ExtractorTemplatesRetrieverService.class);

    private final RetrievalCoreApiService retrievalCoreApiService;
    private final ArticleRepository articleRepository;


    public ArticleUrlsRetrieverService(RetrievalCoreApiService retrievalCoreApiService, ArticleRepository articleRepository) {
        this.retrievalCoreApiService = retrievalCoreApiService;
        this.articleRepository = articleRepository;
    }

    public void process() {
        while(true) {
            if (!articleRepository.existsWaitingProcessingOrProcessedArticles()) {
                int page = 0;
                //for (int page = 0; page < 100; ++page) {
                    //logger.info("Processing page {}", page);
                    logger.info("Retrieving a maximum of 100 URLs from RetrievalCore to be processed");
                    ArticleUrlsDto articleUrlsDto = retrievalCoreApiService.retrieveArticleUrls(page, 100);
                    articleUrlsDto.forEach((templateName, urlList) -> {
                        urlList.forEach(url -> {
                            Article article = new Article();
                            article.setUrl(url);
                            article.setTemplateName(templateName);
                            article.setState(ProcessingState.WAITING);
                            article.setContents(null);
                            articleRepository.save(article);
                        });
                    });
                //}
            } else {
                    logger.info("There are still URLs to be processed. Waiting for 1 minute before considering requesting other URLs from the RetrievalCore");
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        //throw new ArticleInfoSenderException(e);
                    }

            }
        }
    }
}
