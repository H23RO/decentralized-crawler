package ro.h23.dars.retrievalcore.api.scraperapi.controller;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.h23.dars.retrievalcore.api.scraperapi.dto.ArticleInfoDto;
import ro.h23.dars.retrievalcore.api.scraperapi.dto.ExtractorTemplatesDto;
import ro.h23.dars.retrievalcore.common.service.JsonConverterService;
import ro.h23.dars.retrievalcore.service.store.ArticleStoreService;
import ro.h23.dars.retrievalcore.service.store.ArticleStoreServiceException;
import ro.h23.dars.retrievalcore.persistence.model.Page;
import ro.h23.dars.retrievalcore.persistence.model.PageUser;
import ro.h23.dars.retrievalcore.persistence.model.Site;
import ro.h23.dars.retrievalcore.persistence.repository.ArticleRepository;
import ro.h23.dars.retrievalcore.persistence.repository.PageRepository;
import ro.h23.dars.retrievalcore.persistence.repository.PageUserRepository;
import ro.h23.dars.retrievalcore.persistence.repository.SiteRepository;
import ro.h23.dars.retrievalcore.api.scraperapi.dto.ArticleUrlsDto;
import ro.h23.dars.retrievalcore.security.model.UserDetailsImpl;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("hasRole('ROLE_SCRAPER')")
@RestController
@RequestMapping("/api/scraper")
public class ScraperController {

    private static final Logger logger = LoggerFactory.getLogger(ScraperController.class);
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final PageUserRepository pageUserRepository;
    private final ArticleRepository articleRepository;

    private final ArticleStoreService articleStoreService;

    private final JsonConverterService jsonConverterService;

    public ScraperController(SiteRepository siteRepository, PageRepository pageRepository, PageUserRepository pageUserRepository, ArticleRepository articleRepository, ArticleStoreService articleStoreService, JsonConverterService jsonConverterService) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.pageUserRepository = pageUserRepository;
        this.articleRepository = articleRepository;
        this.articleStoreService = articleStoreService;
        this.jsonConverterService = jsonConverterService;
    }

    @GetMapping("extractor-templates")
    public ExtractorTemplatesDto getExtractorTemplates() {
        List<Site> siteList = siteRepository.findAll();
        ExtractorTemplatesDto extractorTemplatesDto = new ExtractorTemplatesDto();
        siteList.forEach(site -> extractorTemplatesDto.put(site.getName(), site.getExtractorTemplate()));
        return extractorTemplatesDto;
    }

    @GetMapping("article-urls")
    //first page is 0 !!!!
    public ArticleUrlsDto getArticleUrls(Authentication authentication, @RequestParam("page") int page,
                                         @RequestParam("size") int size) {

        ArticleUrlsDto articleUrlsDto = new ArticleUrlsDto();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        logger.info("Returning {} article URLs for user {}", size, userId);
        List<PageUser> pageUserList = pageUserRepository.findPageBySiteAndUrlPathParamsNative(userId, PageRequest.of(page, size));
        pageUserList.forEach(pageUser -> {
            Page pageObject = pageUser.getPage();
            Site site = pageObject.getSite();
            String siteName = site.getName();
            articleUrlsDto.putIfAbsent(siteName, new ArrayList<>());
            articleUrlsDto.get(siteName).add(pageObject.getUrlPath());
        } );
        return articleUrlsDto;
    }

    /*@PostMapping("articles")
    public List<String> setArticleInfos(@RequestBody List<ArticleInfoDto> articleInfoDtoList) {
        logger.info("Setting article infos: " + articleInfoDtoList.size());
        return null;
    }*/

    @PostMapping("articles")
    public String setArticleInfos(Authentication authentication, HttpServletRequest request) {
        logger.info("Setting article infos: ");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            InputStream inputStream = request.getInputStream();

            ArchiveInputStream in = new ZipArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(inputStream)));
            ArchiveEntry entry = null;
            while ((entry = in.getNextEntry()) != null) {
                if (!in.canReadEntryData(entry)) {
                    logger.error("Cannot read entry data: " + entry.toString());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read entry data: " + entry.toString());
                }
                String entryName = entry.getName();
                logger.info("Reading entry: " + entryName);
                if (!entryName.endsWith("___image")) {
                    // find the associated site
                    String templateName = entryName.split("___")[0];
                    Site site = siteRepository.findOneByName(templateName);
                    if (site == null) {
                        logger.error("Cannot find site for template name: " + templateName);
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find site for template name: " + templateName);
                    }
                    // obtain the ArticleInfoDto object
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy(in, baos);
                    String jsonString = baos.toString(StandardCharsets.UTF_8);
                    //logger.info(jsonString.toString());
                    ArticleInfoDto articleInfoDto = jsonConverterService.fromJson(jsonString, ArticleInfoDto.class);
                    //logger.info("articleInfoDto: " + articleInfoDto.toString());
                    // get the featured image
                    entry = in.getNextEntry();
                    entryName = entry.getName();
                    byte[] featuredImage = null;
                    if (entryName.endsWith("___image")) {
                        //logger.info("image entry name size: " + entry.getSize());
                        baos = new ByteArrayOutputStream();
                        IOUtils.copy(in, baos);
                        featuredImage = baos.toByteArray();
                        //logger.info("image entry name size: " + featuredImage.length);
                        if (featuredImage.length == 0) {
                            featuredImage = null;
                        }
                    } else {
                        logger.error("Invalid entry name (2): " + entry.getName());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid entry name (2): " + entry.getName());
                    }

                    //
                    try {
                        articleStoreService.store(userId, site, articleInfoDto, featuredImage);
                    } catch(ArticleStoreServiceException e) {
                        logger.error("Exception while storing article info for article: " + articleInfoDto.getUrl(), e);
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception while storing article info for article: " + articleInfoDto.getUrl(), e);
                    }

                } else {
                    logger.error("Invalid entry name: " + entry.getName());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid entry name: " + entry.getName());
                }
            }
            in.close();
        } catch (IOException e) {
            logger.error("Exception while receiving article infos", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception while receiving article infos", e);
        }
        return "ok";
    }

}
