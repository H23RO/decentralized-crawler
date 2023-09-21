package ro.h23.dars.webcrawler.persistence.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.h23.dars.webcrawler.persistence.model.Page;
import ro.h23.dars.webcrawler.persistence.model.PageState;
import ro.h23.dars.webcrawler.persistence.model.Site;

import java.util.Date;
import java.util.List;

public interface PageRepository extends JpaRepository<Page, Integer> {

    @Query("SELECT p FROM Page p WHERE p.pageType = ro.h23.dars.webcrawler.persistence.model.PageType.ARTICLE AND p.state = ro.h23.dars.webcrawler.persistence.model.PageState.PROCESSED ORDER BY p.creationTimestamp ASC")
    List<Page> findAllArticlesThatAreNotSent(Pageable page);

    @Query("SELECT p FROM Page p WHERE p.creationTimestamp > :creationTimestamp AND p.pageType = ro.h23.dars.webcrawler.persistence.model.PageType.ARTICLE ORDER BY p.creationTimestamp ASC")
    List<Page> findAllArticlesWithCreationDateTimeAfter(@Param("creationTimestamp") Date creationTimestamp, Pageable page);

    @Query("SELECT p FROM Page p WHERE p.pageType = ro.h23.dars.webcrawler.persistence.model.PageType.ARTICLE ORDER BY p.creationTimestamp ASC")
    List<Page> findAllArticles(Pageable page);

    Page findBySiteAndUrlPath(Site site, String urlPath);

    @Query(value = "SELECT * FROM page p WHERE p.site_id = :siteId AND p.state = :state ORDER BY p.creation_timestamp ASC LIMIT 1", nativeQuery = true)
    List<Page> internalFindPageBySiteAndStatusNamedParamsNative(@Param("siteId") Integer siteId, @Param("state") String status);

    default Page findPageBySiteAndStatusNamedParamsNative(Site site, PageState status) {
        List<Page> pageList = internalFindPageBySiteAndStatusNamedParamsNative(site.getId(), status.name());
        return (pageList.isEmpty() ? null : pageList.get(0));
    }

    @Query("SELECT p FROM Page p WHERE p.site = :site AND p.state = :state ORDER BY p.creationTimestamp ASC")
    List<Page> internalFindPageBySiteAndStatusNamedParams(@Param("site") Site site, @Param("state") PageState status, Pageable page);

    default Page findPageBySiteAndStatusNamedParams(Site site, PageState status) {
        List<Page> pageList = internalFindPageBySiteAndStatusNamedParams(site, status, PageRequest.of(0, 1));
        return (pageList.isEmpty() ? null : pageList.get(0));
    }
}