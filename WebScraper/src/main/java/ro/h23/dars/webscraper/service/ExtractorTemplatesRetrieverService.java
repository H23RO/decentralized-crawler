package ro.h23.dars.webscraper.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.dto.ExtractorTemplatesDto;
import ro.h23.dars.webscraper.persistence.model.ExtractorTemplate;
import ro.h23.dars.webscraper.persistence.repository.ExtractorTemplateRepository;

@Service
public class ExtractorTemplatesRetrieverService {

    private static final Logger logger = LogManager.getLogger(ExtractorTemplatesRetrieverService.class);

    private final RetrievalCoreApiService retrievalCoreApiService;
    private final ExtractorTemplateRepository extractorTemplateRepository;


    public ExtractorTemplatesRetrieverService(RetrievalCoreApiService retrievalCoreApiService, ExtractorTemplateRepository extractorTemplateRepository) {
        this.retrievalCoreApiService = retrievalCoreApiService;
        this.extractorTemplateRepository = extractorTemplateRepository;
    }

    public void process() {
        ExtractorTemplatesDto extractorTemplatesDto = retrievalCoreApiService.retrieveExtractorTemplates();
        extractorTemplatesDto.forEach((templateName, template) -> {
            ExtractorTemplate extractorTemplate = extractorTemplateRepository.findOneByName(templateName);
            logger.info("Template `{}` exists: {}", templateName, (extractorTemplate != null));
            if (extractorTemplate == null) {
                extractorTemplate = new ExtractorTemplate();
                extractorTemplate.setName(templateName);
            }
            extractorTemplate.setTemplate(template);
            extractorTemplateRepository.save(extractorTemplate);
        });
    }

}
