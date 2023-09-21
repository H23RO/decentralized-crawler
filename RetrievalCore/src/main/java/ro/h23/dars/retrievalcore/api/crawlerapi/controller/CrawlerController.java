package ro.h23.dars.retrievalcore.api.crawlerapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.h23.dars.retrievalcore.api.crawlerapi.dto.PageDto;
import ro.h23.dars.retrievalcore.api.crawlerapi.dto.SiteDto;
import ro.h23.dars.retrievalcore.service.NewPageAddedService;
import ro.h23.dars.retrievalcore.api.crawlerapi.dto.PagesDto;
import ro.h23.dars.retrievalcore.persistence.model.Page;
import ro.h23.dars.retrievalcore.persistence.model.ProcessingState;
import ro.h23.dars.retrievalcore.persistence.model.Site;
import ro.h23.dars.retrievalcore.persistence.repository.PageRepository;
import ro.h23.dars.retrievalcore.persistence.repository.SiteRepository;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ROLE_CRAWLER')")
@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);
    private final SiteRepository siteRepository;

    private final PageRepository pageRepository;

    private final NewPageAddedService newPageAddedService;

    public CrawlerController(SiteRepository siteRepository, PageRepository pageRepository, NewPageAddedService newPageAddedService) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.newPageAddedService = newPageAddedService;
    }

    @GetMapping("sites")
    @ResponseBody
    public List<SiteDto> getSites() {
        logger.info("Sending site list");
        List<Site> siteList = siteRepository.findAll();
        return siteList.stream().map((site -> new SiteDto(site.getName(), site.getUrlBase(), site.getPageTypeClassifier()))).collect(Collectors.toList());
    }

    @PostMapping("pages")
    @ResponseBody
    public String setPages(@RequestBody PagesDto pagesDto) {
        // FIXME

        pagesDto.forEach((siteName, pageDtoList) -> {
            logger.info("Receiving {} pages from site {}", pageDtoList.size(), siteName);
            Site site = siteRepository.findOneByName(siteName);
            if (site == null) {
                // TODO
                throw new RuntimeException("Site `"+siteName+"` not found");
            }
            for (PageDto pageDto : pageDtoList) {
                // find page
                Page page = pageRepository.findPageBySiteAndUrlPathParamsNative(site, pageDto.getUrl());
                if (page == null) {
                    page = new Page();
                    page.setSite(site);
                    page.setUrlPath(pageDto.getUrl());
                    // TODO use the processing time?
                    page.setState(ProcessingState.NEW);
                    // TODO improve this if we have multiple crawlers
                    pageRepository.save(page);
                    newPageAddedService.process(page);
                }
            }
        });




        return "ok";
    }

}
