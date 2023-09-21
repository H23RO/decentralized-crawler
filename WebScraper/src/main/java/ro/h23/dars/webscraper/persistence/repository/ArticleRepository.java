package ro.h23.dars.webscraper.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ro.h23.dars.webscraper.persistence.model.Article;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query("SELECT a FROM Article a WHERE a.state = ro.h23.dars.webscraper.persistence.model.ProcessingState.WAITING ORDER BY a.creationTimestamp DESC")
    List<Article> findAllWaitingArticles(Pageable page);

    @Query("SELECT a FROM Article a WHERE a.state = ro.h23.dars.webscraper.persistence.model.ProcessingState.PROCESSED ORDER BY a.creationTimestamp ASC")
    List<Article> findAllProcessedArticles(Pageable page);

    @Query("SELECT CASE WHEN COUNT(a)> 0 THEN TRUE ELSE FALSE END FROM Article a WHERE a.state = ro.h23.dars.webscraper.persistence.model.ProcessingState.WAITING")
    boolean existsWaitingArticles();

    @Query("SELECT CASE WHEN COUNT(a)> 0 THEN TRUE ELSE FALSE END FROM Article a WHERE a.state = ro.h23.dars.webscraper.persistence.model.ProcessingState.PROCESSED")
    boolean existsProcessedArticles();

    @Query("SELECT CASE WHEN COUNT(a)> 0 THEN TRUE ELSE FALSE END FROM Article a WHERE a.state = ro.h23.dars.webscraper.persistence.model.ProcessingState.WAITING OR a.state = ro.h23.dars.webscraper.persistence.model.ProcessingState.PROCESSING OR a.state = ro.h23.dars.webscraper.persistence.model.ProcessingState.PROCESSED")
    boolean existsWaitingProcessingOrProcessedArticles();
}
