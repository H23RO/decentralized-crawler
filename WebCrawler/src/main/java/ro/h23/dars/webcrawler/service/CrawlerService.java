package ro.h23.dars.webcrawler.service;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.h23.dars.webcrawler.config.CrawlerConfig;
import ro.h23.dars.webcrawler.finder.LinkFinder;
import ro.h23.dars.webcrawler.module.FetcherModule;
import ro.h23.dars.webcrawler.module.ModuleException;
import ro.h23.dars.webcrawler.persistence.model.Page;
import ro.h23.dars.webcrawler.persistence.model.PageState;
import ro.h23.dars.webcrawler.persistence.model.PageType;
import ro.h23.dars.webcrawler.persistence.model.Site;
import ro.h23.dars.webcrawler.persistence.repository.PageRepository;
import ro.h23.dars.webcrawler.data.PageTypeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CrawlerService {

    private static final Logger logger = LogManager.getLogger(CrawlerService.class);

    private static final Random rnd = new Random();

    private final CrawlerConfig crawlerConfig;
    private final PageRepository pageRepository;

    private final JsonConverterService jsonConverterService;

    // FIXME
    private RetrievalCoreApiService retrievalCoreApiService;


    public CrawlerService(CrawlerConfig crawlerConfig, PageRepository pageRepository, JsonConverterService jsonConverterService) {
        this.crawlerConfig = crawlerConfig;
        this.pageRepository = pageRepository;
        this.jsonConverterService = jsonConverterService;
    }

    // update `page` set page_type="UNKNOWN", state="WAITING" where url_path="";
    // SELECT avg(number_of_links), count(*), site_id, page_type, state FROM `page` group by site_id, page_type, state ORDER BY site_id, page_type, state;
    //SELECT s.name, avg(number_of_links), count(*), page_type, state FROM `page` p, site s where p.site_id=s.id group by site_id, page_type, state ORDER BY site_id, page_type, state;
    @SneakyThrows
    public void crawl(final Site site) {
        PageTypeClassifier pageTypeClassifier = jsonConverterService.fromJson(site.getPageTypeClassifier(), PageTypeClassifier.class);
        String urlBase = site.getUrlBase() + "/";
        int urlBaseLength = urlBase.length();
        FetcherModule fetcherModule = new FetcherModule();
        logger.info("Started crawler for  " + urlBase);
        File outputDir = new File("output");
        outputDir.mkdirs();
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(outputDir, site.getName()+".log"), false), true);



        do {

            long timeTotal = System.currentTimeMillis();
            long timeRetrievePage = timeTotal;
            //Page page = pageRepository.findPageBySiteAndStatusNamedParams(site, PageState.WAITING);
            Page page = pageRepository.findPageBySiteAndStatusNamedParamsNative(site, PageState.WAITING);
            timeRetrievePage = System.currentTimeMillis() - timeRetrievePage;

            int foundLinksCount = 0;
            int foundLinksWrittenCount = 0;

            if (page != null) {
                long timeSaveProcessing = System.currentTimeMillis();
                page.setState(PageState.PROCESSING);
                pageRepository.save(page);
                timeSaveProcessing = System.currentTimeMillis() - timeSaveProcessing;


                long timeGetContents = System.currentTimeMillis();
                URL pageUrl;
                String pageUrlString = urlBase + page.getUrlPath();
                if (!pageUrlString.contains("tel:+")) {
                    logger.info("Crawling page " + pageUrlString);
                }
                try {
                    pageUrl = new URL(pageUrlString);
                } catch (MalformedURLException e) {
                    logger.error("Invalid URL for page: " + page.getUrlPath() + "; siteUrlBase: " + urlBase, e);
                    page.setState(PageState.INVALID);
                    page.setDetails("Invalid URL for page: " + page.getUrlPath() + "; siteUrlBase: " + urlBase);
                    pageRepository.save(page);
                    // process next page
                    continue;
                }
                /*try {
                    fetcherModule.process(pageUrl);
                } catch (ModuleException e) {
                    if (!pageUrlString.contains("tel:+")) {
                        logger.error(e.getMessage(), e);
                    }
                    page.setState(PageState.INVALID);
                    page.setDetails(e.getMessage());
                    pageRepository.save(page);
                    // process next page
                    continue;
                }*/
                String pageContents = null;
                try {
                    pageContents = fetcherModule.process(pageUrl);
                } catch (ModuleException e) {
                    if (!pageUrlString.contains("tel:+")) {
                        logger.error(e.getMessage(), e);
                    }
                    page.setState(PageState.INVALID);
                    page.setDetails(e.getMessage());
                    pageRepository.save(page);
                    // process next page
                    continue;
                }
                timeGetContents = System.currentTimeMillis() - timeGetContents;

                long timeClassify = System.currentTimeMillis();
                // classify
                PageType pageType;
                if (pageTypeClassifier.getContainsList().isEmpty() && pageTypeClassifier.getContainsNotList().isEmpty()) {
                    pageType = PageType.IRRELEVANT;
                } else {
                    pageType = PageType.ARTICLE;
                    for (String text : pageTypeClassifier.getContainsList()) {
                        if (!pageContents.contains(text)) {
                            pageType = PageType.IRRELEVANT;
                            break;
                        }
                    }
                    for (String text : pageTypeClassifier.getContainsNotList()) {
                        if (pageContents.contains(text)) {
                            pageType = PageType.IRRELEVANT;
                            break;
                        }
                    }
                }
                page.setPageType(pageType);
                pageRepository.save(page);
                timeClassify = System.currentTimeMillis() - timeClassify;

                long timeFindLinks = System.currentTimeMillis();
                // find links
                //System.out.println("xxxxx: " + pageUrlString);
                LinkFinder linkFinder = new LinkFinder(pageUrlString, pageContents);
                String link;
                List<Page> newPageList = new ArrayList<>();
                while ((link = linkFinder.next()) != null) {
                    // check if the link is in the current site
                    //if (link.replaceFirst(".*href=\"([^\"]*)\".*", "$1").equals(siteUrlBase)) {
                    //https://www.lexshop.ro/?page=produse&categorie=8&n=1
                    ++foundLinksCount;
                    //log.trace("fulllink: {}", link);
                    //TODO
                    if (urlBase.startsWith("https://") && link.startsWith("http://")) {
                        link = "https://" + link.substring(7);
                    }

                    //if (link.matches(regExString)) {
                    if (link.startsWith(urlBase)) {
                        // 2.3.1. If the page does not exist in the collection, then it is added and the status is set to WAITING.
                        //logger.info("Found link - match: {}", link);
                        int x = link.indexOf("&zenid=");
                        if (x != -1) {
                            link = link.substring(0, x);
                        }
                        //dbManager.addPage(new Page(siteId, link.substring(urlBaseLength + 1), new Date()));
                        String newUrlPath = link.substring(urlBaseLength);
                        if (pageRepository.findBySiteAndUrlPath(site, newUrlPath) == null) {
                            try {
                                ++foundLinksWrittenCount;
                                Page newPage = new Page();
                                newPage.setSite(site);
                                newPage.setUrlPath(newUrlPath);
                                //newPageList.add(newPage);
                                //if (newPageList.size() > 100) {
                                    pageRepository.save(newPage);
                                    //pageRepository.saveAll(newPageList);
                                   // newPageList.clear();
                                //}
                            } catch(Exception e) {
                                e.printStackTrace();
                                //javax.validation.ConstraintViolationException:} catch (DataIntegrityViolationException e) {
                                // just ignore because the link already exists
                                //TODO use a hashset/patricia trie to store all the links?
                            }
                        }
                    } else {
                        //TODO
                        // throw new CrawlerException("Found valid link does not start with the site url base. Link: " + link + "; urlBase: " + urlBase);
                    }
                    //} else {
                    // 2.3.2. Otherwise, it is ignored.
                    //log.trace("Found link - no match: {}", link);
                    //}
                }
                if (newPageList.size() > 0) {
                    //pageRepository.save(newPage);
                    pageRepository.saveAll(newPageList);
                    newPageList.clear();
                }
                timeFindLinks = System.currentTimeMillis() - timeFindLinks;

                long timeSaveProcessed = System.currentTimeMillis();
                page.setState(PageState.PROCESSED);
                page.setDetails(null);
                page.setNumberOfLinks(foundLinksCount);
                pageRepository.save(page);
                timeSaveProcessed = System.currentTimeMillis() - timeSaveProcessed;

                timeTotal = System.currentTimeMillis() - timeTotal;

                pw.println(site.getName() + "; " + timeRetrievePage + "; " + timeSaveProcessing + "; " + timeGetContents + "; " + timeClassify + "; " + timeFindLinks + "; " + timeSaveProcessed + "; " + timeTotal + "; " + foundLinksCount + "; " + foundLinksWrittenCount + "; " + pageContents.length());


                try {
                    long sleepTime = crawlerConfig.getSiteWaitTimeMin() + rnd.nextInt(crawlerConfig.getSiteWaitTimeMax() - crawlerConfig.getSiteWaitTimeMin());
                    logger.info("sleeping for {} ms", sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        } while (true);
        logger.info("Crawling for site {} complete.", site.getName());
    }
}
