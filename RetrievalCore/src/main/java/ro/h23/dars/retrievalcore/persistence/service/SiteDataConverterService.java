package ro.h23.dars.retrievalcore.persistence.service;

import org.springframework.stereotype.Service;
import ro.h23.dars.retrievalcore.common.service.JsonConverterService;
import ro.h23.dars.retrievalcore.config.model.SiteInfo;
import ro.h23.dars.retrievalcore.persistence.model.Site;

@Service
public class SiteDataConverterService {

    private final JsonConverterService jsonConverterService;

    public SiteDataConverterService(JsonConverterService jsonConverterService) {
        this.jsonConverterService = jsonConverterService;
    }

    public Site siteInfoToSite(SiteInfo siteInfo) {
        Site site = new Site();
        site.setName(siteInfo.getName());
        site.setUrlBase(siteInfo.getUrlBase());
        site.setLogoUrl(siteInfo.getLogoUrl());
        site.setPageTypeClassifier(jsonConverterService.toJson(siteInfo.getPageTypeClassifier()));
        site.setExtractorTemplate(jsonConverterService.toJson(siteInfo.getExtractorTemplate()));
        return site;
    }

    /**
     * Updates the site variable received as argument and returns it.
     * If the site argument is null, then a site Object is created
     */
    public Site siteInfoToExistingSite(SiteInfo siteInfo, Site site) {
        if (site == null) {
            site = new Site();
        }
        site.setName(siteInfo.getName());
        site.setUrlBase(siteInfo.getUrlBase());
        site.setLogoUrl(siteInfo.getLogoUrl());
        site.setPageTypeClassifier(jsonConverterService.toJson(siteInfo.getPageTypeClassifier()));
        site.setExtractorTemplate(jsonConverterService.toJson(siteInfo.getExtractorTemplate()));
        return site;
    }

}
