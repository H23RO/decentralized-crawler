package ro.h23.dars.webcrawler.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.h23.dars.webcrawler.persistence.model.Site;

public interface SiteRepository extends JpaRepository<Site, Integer> {

    Site findOneByName(String name);
}
