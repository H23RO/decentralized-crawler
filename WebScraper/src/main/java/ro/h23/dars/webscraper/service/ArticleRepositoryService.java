package ro.h23.dars.webscraper.service;

import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.persistence.model.Article;
import ro.h23.dars.webscraper.persistence.repository.ArticleRepository;

@Service
public class ArticleRepositoryService {

    private final ArticleRepository articleRepository;

    //private BlockingQueue<Article> articleQueue;

    public ArticleRepositoryService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
        //this.articleQueue = new LinkedBlockingQueue<>();
    }

    public synchronized void save(Article article) {
        articleRepository.save(article);
        /*try {
            this.articleQueue.put(article);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
    }
}
