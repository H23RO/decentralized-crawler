package ro.h23.dars.webscraper.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.h23.dars.webscraper.persistence.model.ExtractorTemplate;

public interface ExtractorTemplateRepository extends JpaRepository<ExtractorTemplate, Integer> {

    ExtractorTemplate findOneByName(String name);
}
