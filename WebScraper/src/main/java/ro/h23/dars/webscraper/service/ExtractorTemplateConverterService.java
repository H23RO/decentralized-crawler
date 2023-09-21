package ro.h23.dars.webscraper.service;

import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.common.service.JsonConverterService;
import ro.h23.dars.webscraper.data.ExtractorTemplateInfo;

@Service
public class ExtractorTemplateConverterService {

    private final JsonConverterService jsonConverterService;

    public ExtractorTemplateConverterService(JsonConverterService jsonConverterService) {
        this.jsonConverterService = jsonConverterService;
    }

    public ExtractorTemplateInfo convert(String extractorTemplateString) {
        return jsonConverterService.fromJson(extractorTemplateString, ExtractorTemplateInfo.class);
    }
}
