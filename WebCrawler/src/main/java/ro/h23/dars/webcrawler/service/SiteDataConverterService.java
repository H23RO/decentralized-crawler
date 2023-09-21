package ro.h23.dars.webcrawler.service;

import org.springframework.stereotype.Service;
import ro.h23.dars.webcrawler.dto.PageDto;
import ro.h23.dars.webcrawler.dto.SiteDto;
import ro.h23.dars.webcrawler.persistence.model.Page;
import ro.h23.dars.webcrawler.persistence.model.Site;

@Service
public class SiteDataConverterService {

    private final JsonConverterService jsonConverterService;

    public SiteDataConverterService(JsonConverterService jsonConverterService) {
        this.jsonConverterService = jsonConverterService;
    }

    public Site siteDtoToSite(SiteDto siteDto) {
        Site site = new Site();
        site.setName(siteDto.getName());
        site.setUrlBase(siteDto.getUrlBase());
        site.setPageTypeClassifier(jsonConverterService.toJson(siteDto.getPageTypeClassifier()));
        return site;
    }

    /**
     * Updates the site variable received as argument and returns it.
     * If the site argument is null, then a site Object is created
     */
    public Site siteDtoToExistingSite(SiteDto siteDto, Site site) {
        if (site == null) {
            site = new Site();
        }
        site.setName(siteDto.getName());
        site.setUrlBase(siteDto.getUrlBase());
        //site.setPageTypeClassifier(jsonConverterService.toJson(siteDto.getPageTypeClassifier()));
        site.setPageTypeClassifier(siteDto.getPageTypeClassifier());
        return site;
    }

    public PageDto pageToPageDto(Page page) {
        return new PageDto(page.getSite().getUrlBase() + "/" + page.getUrlPath(), page.getCreationTimestamp());
    }

}
