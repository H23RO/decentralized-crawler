package ro.h23.dars.webscraper.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.common.service.JsonConverterService;
import ro.h23.dars.webscraper.config.ScraperConfig;
import ro.h23.dars.webscraper.exception.ArticleInfoSenderException;
import ro.h23.dars.webscraper.persistence.model.Article;
import ro.h23.dars.webscraper.persistence.model.ProcessingState;
import ro.h23.dars.webscraper.persistence.repository.ArticleRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ArticleInfoSenderService {

    private static final Logger logger = LogManager.getLogger(ArticleInfoSenderService.class);
    private final JsonConverterService jsonConverterService;
    private final ScraperConfig scraperConfig;

    private final ArticleRepository articleRepository;

    private final RetrievalCoreApiService retrievalCoreApiService;

    private final ArticleListCompressionService articleListCompressionService;

    public ArticleInfoSenderService(JsonConverterService jsonConverterService, ScraperConfig scraperConfig, ArticleRepository articleRepository, RetrievalCoreApiService retrievalCoreApiService, ArticleListCompressionService articleListCompressionService) {
        this.jsonConverterService = jsonConverterService;
        this.scraperConfig = scraperConfig;
        this.articleRepository = articleRepository;
        this.retrievalCoreApiService = retrievalCoreApiService;
        this.articleListCompressionService = articleListCompressionService;
    }

    public void process() throws ArticleInfoSenderException {



        //TODO maybe paginate
        //List<Article> articleList = articleRepository.findAllProcessedArticles(Pageable.unpaged());
        do {
            while (!articleRepository.existsProcessedArticles()) {
                logger.info("There are no processed articles. Waiting for 1 minute for new articles to be sent");
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new ArticleInfoSenderException(e);
                }
            }

            List<Article> articleList = articleRepository.findAllProcessedArticles(Pageable.ofSize(20));
            logger.info("Sending a batch of " + ((articleList != null)?articleList.size():"null") + " articles");
            if (articleList == null || articleList.size() <= 0) {
                break;
            }

            // send all the articles
        /*
        List<ArticleInfoDto> articleInfoDtoList = new ArrayList<>();
        for (Article article : articleList) {
            int articleId = article.getId();
            try {

                String articleJsonString = Files.readString(new File(scraperConfig.getOutputDir(), articleId + ".json").toPath(), StandardCharsets.UTF_8);
                ArticleInfoDto articleInfoDto = jsonConverterService.fromJson(articleJsonString, ArticleInfoDto.class);
                articleInfoDtoList.add(articleInfoDto);
                // TODO ?

            } catch (IOException e) {
                article.setState(ProcessingState.UNKNOWN);
                articleRepository.save(article);
                throw new ArticleInfoSenderException(e);
            }
        }
        retrievalCoreApiService.sendArticleInfos(articleInfoDtoList);
        */

            try {
                byte[] data = articleListCompressionService.compress(scraperConfig.getOutputDir(), articleList);
                //System.out.println(">>>>>>>>> " + data.length);
                String responseString = retrievalCoreApiService.sendArticleInfos(data);
                if (responseString.equals("ok")) {
                    // TODO
                    logger.info("Response is ok");
                    for (Article article : articleList) {
                        article.setState(ProcessingState.SENT);
                    }
                    articleRepository.saveAll(articleList);

                }
            } catch (IOException e) {
                System.out.println("!!!!!!!!!!!!!");
                throw new ArticleInfoSenderException(e);
            }

        } while(true);
        /*
        for (Article article : articleList) {
            if (article.getState() != ProcessingState.UNKNOWN) {
                article.setState(ProcessingState.SENT);
            }
        }
        articleRepository.saveAll(articleList);
        */
    }
}
