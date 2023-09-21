package ro.h23.dars.retrievalcore.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.h23.dars.retrievalcore.persistence.model.PageUser;

import java.util.List;


public interface PageUserRepository extends JpaRepository<PageUser, Long>  {
    @Query(value = "SELECT * FROM page_user x WHERE x.user_id = :userId ORDER BY creation_timestamp ASC", nativeQuery = true)
    List<PageUser> findPageBySiteAndUrlPathParamsNative(@Param("userId") Long userId, Pageable page);

    @Query(value = "SELECT * FROM page_user x WHERE x.user_id = :userId and x.page_id = :pageId ORDER BY creation_timestamp ASC", nativeQuery = true)
    PageUser findByPageAndUserNative(@Param("pageId") Long pageId, @Param("userId") Long userId);

}
