package ro.h23.dars.retrievalcore.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.h23.dars.retrievalcore.auth.persistence.model.User;
import ro.h23.dars.retrievalcore.persistence.model.Site;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Site findOneByName(String name);

    @Query(value = "SELECT * FROM site x WHERE x.user_id = :userId ORDER BY creation_timestamp ASC", nativeQuery = true)
    List<Site> findSitesByUserIdNative(@Param("userId") Long userId);
}
