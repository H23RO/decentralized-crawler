package ro.h23.dars.webscraper.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.data.ExtractorTemplateInfo;
import ro.h23.dars.webscraper.persistence.model.Article;
import ro.h23.dars.webscraper.persistence.repository.ArticleRepository;
import ro.h23.dars.webscraper.persistence.repository.ExtractorTemplateRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScraperManagerService {
    private static final Logger logger = LogManager.getLogger(ScraperManagerService.class);
    private final ArticleRepository articleRepository;

    private final ExtractorTemplateRepository extractorTemplateRepository;

    private final ExtractorTemplateConverterService extractorTemplateConverterService;
    private final ScraperService scraperService;



    public ScraperManagerService(ArticleRepository articleRepository, ExtractorTemplateRepository extractorTemplateRepository, ExtractorTemplateConverterService extractorTemplateConverterService, ScraperService scraperService) {
        this.articleRepository = articleRepository;
        this.extractorTemplateRepository = extractorTemplateRepository;
        this.extractorTemplateConverterService = extractorTemplateConverterService;
        this.scraperService = scraperService;
    }

    public void process() {
        // check in the database for article URLs in WAITING state
        // TODO @ pageable
        while(true) {
            List<Article> articleList = articleRepository.findAllWaitingArticles(Pageable.unpaged());
            logger.info("Found {} article URLs in WAITING state", articleList.size());
            Map<String, List<Article>> templateToArticleListMap = new HashMap<>();
            Map<String, ExtractorTemplateInfo> templateNameToExtractorTemplateInfoMap = new HashMap<>();
            // create a map: templateName -> article list
            articleList.forEach(article -> {
                String templateName = article.getTemplateName();
                if (templateNameToExtractorTemplateInfoMap.get(templateName) == null) {
                    templateNameToExtractorTemplateInfoMap.put(templateName, extractorTemplateConverterService.convert(extractorTemplateRepository.findOneByName(templateName).getTemplate()));
                }

                List<Article> articleListForTemplate = templateToArticleListMap.computeIfAbsent(templateName, k -> new ArrayList<>());
                articleListForTemplate.add(article);
            });
            //
            List<Thread> threadList = new ArrayList<>();
            templateToArticleListMap.forEach((templateName, articleListForTemplate) -> {
                Thread t = new Thread(() -> {
                    scraperService.scrap(templateName, templateNameToExtractorTemplateInfoMap.get(templateName), articleListForTemplate);
                });
                logger.info("Starting Thread-" + templateName + " to process " + articleListForTemplate.size() + " article URLs");
                t.setName("Thread-" + templateName);
                threadList.add(t);
                t.start();
            });
            threadList.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            //
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
