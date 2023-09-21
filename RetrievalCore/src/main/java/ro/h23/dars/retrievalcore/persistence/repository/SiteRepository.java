package ro.h23.dars.retrievalcore.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.h23.dars.retrievalcore.persistence.model.Site;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Site findOneByName(String name);
}
