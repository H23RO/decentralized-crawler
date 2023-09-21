package ro.h23.dars.webcrawler.service;

import org.springframework.stereotype.Service;
import ro.h23.dars.webcrawler.dto.SiteDto;
import ro.h23.dars.webcrawler.persistence.model.Page;
import ro.h23.dars.webcrawler.persistence.model.PageState;
import ro.h23.dars.webcrawler.persistence.model.Site;
import ro.h23.dars.webcrawler.persistence.repository.PageRepository;
import ro.h23.dars.webcrawler.persistence.repository.SiteRepository;

import java.util.List;

@Service
public class BootstrapService {

    private final RetrievalCoreApiService retrievalCoreApiService;

    private final SiteDataConverterService siteDataConverterService;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;

    public BootstrapService(RetrievalCoreApiService retrievalCoreApiService, SiteDataConverterService siteDataConverterService, SiteRepository siteRepository, PageRepository pageRepository) {
        this.retrievalCoreApiService = retrievalCoreApiService;
        this.siteDataConverterService = siteDataConverterService;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
    }

    public void start() {
        List<SiteDto> siteDtoList = retrievalCoreApiService.retrieveSites();
        for (SiteDto siteDto : siteDtoList) {
            Site site = siteRepository.findOneByName(siteDto.getName());
            boolean isNew = (site == null);
            site = siteDataConverterService.siteDtoToExistingSite(siteDto, site);
            siteRepository.save(site);

            if (isNew) {
                Page page = new Page();
                page.setSite(site);
                page.setState(PageState.WAITING);
                page.setUrlPath("");
                pageRepository.save(page);
            }
        }
    }


}
