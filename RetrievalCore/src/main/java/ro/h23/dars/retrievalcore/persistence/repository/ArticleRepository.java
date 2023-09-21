package ro.h23.dars.retrievalcore.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ro.h23.dars.retrievalcore.persistence.model.Article;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {


    @Query("SELECT a FROM Article a WHERE a.state = ro.h23.dars.retrievalcore.persistence.model.ArticleState.VERIFIED ORDER BY a.updateTimestamp ASC")
    List<Article> findAllVerifiedArticles(Pageable page);

    Article findFirstByUrl(String url);


}
