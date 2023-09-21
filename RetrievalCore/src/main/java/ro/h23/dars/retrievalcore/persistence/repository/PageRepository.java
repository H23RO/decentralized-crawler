package ro.h23.dars.retrievalcore.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.h23.dars.retrievalcore.persistence.model.Page;
import ro.h23.dars.retrievalcore.persistence.model.ProcessingState;
import ro.h23.dars.retrievalcore.persistence.model.Site;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

    @Query(value = "SELECT * FROM page p WHERE p.site_id = :siteId AND p.url_path = :urlPath LIMIT 1", nativeQuery = true)
    List<Page> internalFindPageBySiteAndUrlPathParamsNative(@Param("siteId") Integer siteId, @Param("urlPath") String urlPath);

    default Page findPageBySiteAndUrlPathParamsNative(Site site, String urlPath) {
        List<Page> pageList = internalFindPageBySiteAndUrlPathParamsNative(site.getId(), urlPath);
        return (pageList.isEmpty() ? null : pageList.get(0));
    }

    List<Page> findAllByState(ProcessingState processingState, Pageable page);

    Page findPageByUrlPath(String urlPath);
}

