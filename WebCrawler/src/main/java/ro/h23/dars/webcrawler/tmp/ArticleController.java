package ro.h23.dars.webcrawler.tmp;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ro.h23.dars.webcrawler.dto.PageDto;
import ro.h23.dars.webcrawler.persistence.model.Page;
import ro.h23.dars.webcrawler.persistence.repository.PageRepository;
import ro.h23.dars.webcrawler.service.SiteDataConverterService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private static final Logger logger = LogManager.getLogger(ArticleController.class);


    private final PageRepository pageRepository;
    private final SiteDataConverterService siteDataConverterService;

    public ArticleController(PageRepository pageRepository, SiteDataConverterService siteDataConverterService) {
        this.pageRepository = pageRepository;
        this.siteDataConverterService = siteDataConverterService;
    }

    // usage: http://localhost:5041/api/v1/articles?since=1972-01-01T00:00:00&page=0&size=10
    @GetMapping("")
    @ResponseBody
    public List<PageDto> getArticles(@RequestParam(name="since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sinceTimestamp, Pageable pageable) {
        logger.info("GET articles request");
        List<Page> pageList = pageRepository.findAllArticlesWithCreationDateTimeAfter(java.util.Date
                .from(sinceTimestamp.atZone(ZoneId.systemDefault()).toInstant()), pageable);
        return pageList.stream().map(siteDataConverterService::pageToPageDto).collect(Collectors.toList());
    }
}