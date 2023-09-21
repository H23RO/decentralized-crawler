package ro.h23.dars.webcrawler.tmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ro.h23.dars.webcrawler.config.CrawlerConfig;
import ro.h23.dars.webcrawler.module.FetcherModule;
import ro.h23.dars.webcrawler.persistence.model.Page;
import ro.h23.dars.webcrawler.persistence.model.Site;
import ro.h23.dars.webcrawler.persistence.repository.PageRepository;

import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

@Service
public class ScrapperService {

    private static final Logger logger = LogManager.getLogger(ScrapperService.class);

    private static final Random rnd = new Random();

    private final CrawlerConfig crawlerConfig;
    private final PageRepository pageRepository;


    @Autowired
    public ScrapperService(CrawlerConfig crawlerConfig, PageRepository pageRepository) {
        this.crawlerConfig = crawlerConfig;
        this.pageRepository = pageRepository;
    }

    public void scrap(Site site) {

        String urlBase = site.getUrlBase() + "/";
        int urlBaseLength = urlBase.length();
        FetcherModule fetcherModule = new FetcherModule();
        int pageNumber = -1;
        do {
            //Page page = pageRepository.findPageBySiteAndStatusNamedParams(site, PageState.WAITING);
            ++pageNumber;
            List<Page> pageList = pageRepository.findAllArticles(PageRequest.of(pageNumber, 10));
            if (pageList != null && !pageList.isEmpty()) {
                for (Page page : pageList) {
                    URL pageUrl;
                    String pageUrlString = urlBase + page.getUrlPath();
                    logger.info("Processing URL for page: " + pageUrlString);
                    try {
                        pageUrl = new URL(pageUrlString);
                    } catch (MalformedURLException e) {
                        logger.error("Invalid URL for page: " + page.getUrlPath() + "; siteUrlBase: " + urlBase, e);
                        // process next page
                        continue;
                    }
                    try {

                        Document pageDocument = Jsoup.connect(pageUrlString).get();
                        pageDocument.select("script, style, div.ad-wrapper").remove();
                        removeComments(pageDocument);
                        String articleTitle = pageDocument.select("article.article h1").get(0).html();
                        //String articleContents = pageDocument.select("article.article>div.container>div.flex:nth-child(2)>div>div.flex>div").get(0).html();
                        String articleContents = pageDocument.select("#article-content > article > div > div.flex.flex-end.flex-center-md.flex-stretch > div.col-8.col-md-9.col-sm-12 > div > div > div.entry.data-app-meta.data-app-meta-article").get(0).html();

                        PrintWriter pw = new PrintWriter(new File("output/"+page.getId()));
                        pw.println(pageUrlString);
                        pw.println(articleTitle);
                        pw.println(articleContents);
                        pw.close();
                    } catch (Exception e) {
                        logger.error("Error extracting from URL: " + pageUrlString, e);
                        // process next page
                        continue;
                    }
                    //TODO call sleep if error
                    try {
                        long sleepTime = crawlerConfig.getSiteWaitTimeMin() + rnd.nextInt(crawlerConfig.getSiteWaitTimeMax() - crawlerConfig.getSiteWaitTimeMin());
                        logger.info("sleeping for {} ms", sleepTime);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //System.exit(1);
                }
            } else {
                break;
            }
        }  while(true);
        logger.info("Scrapping for site {} complete.", site.getName());
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }
}
