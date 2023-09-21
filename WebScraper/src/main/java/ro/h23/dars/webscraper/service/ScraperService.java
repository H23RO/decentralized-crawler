package ro.h23.dars.webscraper.service;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.config.ScraperConfig;
import ro.h23.dars.webscraper.data.ArticleInfo;
import ro.h23.dars.webscraper.data.ExtractorTemplateInfo;
import ro.h23.dars.webscraper.data.WebResource;
import ro.h23.dars.webscraper.exception.ArticleInfoSaverException;
import ro.h23.dars.webscraper.exception.ScraperException;
import ro.h23.dars.webscraper.persistence.model.Article;
import ro.h23.dars.webscraper.persistence.model.ProcessingState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {

    private static final Logger logger = LogManager.getLogger(ScraperService.class);

    private static final Random rnd = new Random();

    private final ScraperConfig scraperConfig;

    private final ArticleRepositoryService articleRepositoryService;

    private final ArticleInfoSaverService articleInfoSaverService;

    private final HashService hashService;

    private final WebResourceRetrieverService webResourceRetrieverService;

    private final WebResourceStoreService webResourceStoreService;

    public ScraperService(ScraperConfig scraperConfig, ArticleRepositoryService articleRepositoryService, ArticleInfoSaverService articleInfoSaverService, HashService hashService, WebResourceRetrieverService webResourceRetrieverService, WebResourceStoreService webResourceStoreService) {
        this.scraperConfig = scraperConfig;
        this.articleRepositoryService = articleRepositoryService;
        this.articleInfoSaverService = articleInfoSaverService;
        this.hashService = hashService;
        this.webResourceRetrieverService = webResourceRetrieverService;
        this.webResourceStoreService = webResourceStoreService;
    }

    @SneakyThrows
    public void scrap(String templateName, ExtractorTemplateInfo extractorTemplateInfo, List<Article> articleList) {
        String outputDir = scraperConfig.getOutputDir() + "/" + templateName;
        File outputLogDir = new File("output");
        outputLogDir.mkdirs();
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(outputLogDir, "output.log"), true), true);

        for (Article article : articleList) {

            long timeTotal = System.currentTimeMillis();

            String urlString = article.getUrl();
            URL url;
            logger.info("Processing URL: " + urlString);
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                logger.error("Invalid URL: " + urlString, e);
                article.setState(ProcessingState.INVALID);
                articleRepositoryService.save(article);
                // process next page
                continue;
            }
            try {

                Document pageDocument = Jsoup.connect(urlString).get();
                pageDocument.select(extractorTemplateInfo.getRemoveElements()[0]).remove();
                removeComments(pageDocument);

                String articleTitle = extractFromDocument(pageDocument, urlString, extractorTemplateInfo.getTitle());
                String articleContents = extractFromDocument(pageDocument, urlString, extractorTemplateInfo.getContents());
                String[] articleContentsExtractorTemplate = Arrays.copyOf(extractorTemplateInfo.getContents(), extractorTemplateInfo.getContents().length);
                articleContentsExtractorTemplate[1] = "text";
                String articleContentsTextOnly = extractFromDocument(pageDocument, urlString, articleContentsExtractorTemplate);
                String articleFeaturedImageUrlString = extractFromDocument(pageDocument, urlString, extractorTemplateInfo.getFeaturedImage());
                WebResource articleFeaturedImageWebResource = null;
                try {
                    if (articleFeaturedImageUrlString != null) {
                        articleFeaturedImageWebResource = webResourceRetrieverService.retrieve(articleFeaturedImageUrlString);
                    }
                } catch(Exception e) {
                    logger.error("Invalid featured image url: " + articleFeaturedImageUrlString, e);
                }

                String articlePublishDate = extractFromDocument(pageDocument, urlString, extractorTemplateInfo.getPublishDate());
                String articleAuthor = extractFromDocument(pageDocument, urlString, extractorTemplateInfo.getAuthor());

                ArticleInfo articleInfo = new ArticleInfo();
                articleInfo.setUrl(urlString);
                articleInfo.setTitle(articleTitle);
                articleInfo.setContents(articleContents);
                articleInfo.setContentsTextOnly(articleContentsTextOnly);
                articleInfo.setContentsTextOnlyHash(hashService.computeHash(articleContentsTextOnly));
                articleInfo.setFeaturedImageUrl(articleFeaturedImageUrlString);
                if (articleFeaturedImageWebResource != null) {
                    articleInfo.setFeaturedImageHash(hashService.computeHash(articleFeaturedImageWebResource.getContents()));
                } else {
                    articleInfo.setFeaturedImageHash(null);
                }
                articleInfo.setPublishDate(articlePublishDate);
                articleInfo.setAuthor(articleAuthor);
                articleInfo.setExtractedDate(new Date());

                try {
                    articleInfoSaverService.save(outputDir, article.getId(), articleInfo);
                    article.setState(ProcessingState.PROCESSED);
                    articleRepositoryService.save(article);
                    if (articleFeaturedImageWebResource != null) {
                        FileUtils.writeByteArrayToFile(new File(outputDir, "" + article.getId()), articleFeaturedImageWebResource.getContents());
                        webResourceStoreService.store(outputDir, "" + article.getId(), articleFeaturedImageWebResource);
                    }
                    timeTotal = System.currentTimeMillis() - timeTotal;
                    pw.println(timeTotal + "; " + urlString);
                } catch(ArticleInfoSaverException e) {
                    logger.error("Could not save article for URL: " + urlString, e);
                    article.setState(ProcessingState.INVALID);
                    articleRepositoryService.save(article);
                }

            } catch (Exception e) {
                logger.error("Error extracting from URL: " + urlString, e);
                article.setState(ProcessingState.INVALID);
                articleRepositoryService.save(article);
                // process next page
                //continue;
            } finally {
                try {
                    long sleepTime = scraperConfig.getSiteWaitTimeMin() + rnd.nextInt(scraperConfig.getSiteWaitTimeMax() - scraperConfig.getSiteWaitTimeMin());
                    logger.info("sleeping for {} ms", sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.exit(1);
        }

        logger.info("Scrapping for template {} complete.", templateName);
    }

    private String extractFromDocument(Document pageDocument, String urlString, String[] extractionArray) throws ScraperException {
        if (extractionArray == null) {
            return null;
        }
        if (extractionArray.length < 2) {
            throw new ScraperException("Not supported extraction array: " + Arrays.toString(extractionArray));
        }
        String cssQuery = extractionArray[0];
        String property = extractionArray[1];
        boolean canBeMissing = false;
        if (extractionArray.length >= 3) {
            canBeMissing = extractionArray[2].equals("true");
        }
        String regexString = null;
        if (extractionArray.length >= 4) {
            regexString = extractionArray[3];
        }
        try {
            String result = extractFromDocumentImpl(pageDocument, urlString, cssQuery, property, regexString, extractionArray);
            return result;
        } catch (ScraperException e) {
            if (canBeMissing) {
                //logger.warn("Warning extracting from URL: " + urlString, e);
                //FIXME remove this logger.warn("Warning extracting from URL: " + urlString + ". " + e.getMessage());
                return null;
            } else {
                //FIXME throw e;
                return null;
            }
        }
    }

    private String extractFromDocumentImpl(Document pageDocument, String urlString, String cssQuery, String property, String regexString, String[] extractionArray) throws ScraperException {
        String result = null;
        try {
            if (property.equals("text")) {
                result = pageDocument.select(cssQuery).get(0).text();
            } else if (property.equals("html")) {
                result = pageDocument.select(cssQuery).get(0).html();
            } else if (property.startsWith("attr:")) {
                property = property.substring("attr:".length());
                result = pageDocument.select(cssQuery).get(0).attr(property);
                if (property.equals("src") || property.equals("href")) {
                    result = new URI(urlString).resolve(result).toString();
                }
            } else {
                throw new ScraperException("Invalid extraction property for extraction array: " + Arrays.toString(extractionArray));
            }
            if (regexString != null) {
                Matcher matcher = Pattern.compile(regexString).matcher(result);
                if (!matcher.find()) {
                    throw new ScraperException("RegEx expression not matched for `" + result + "` properly for extraction array: " + Arrays.toString(extractionArray));
                }
                String result2 = matcher.group(1);
                if (result2 == null || result2.isEmpty()) {
                    throw new ScraperException("RegEx expression not matched any group for `" + result + "` properly for extraction array: " + Arrays.toString(extractionArray));
                }
                return result2;
            } else {
                return result;
            }
        } catch(IndexOutOfBoundsException e) {
            //throw new ScraperException("Probably the page cannot be extracted properly for extraction array: " + Arrays.toString(extractionArray));
            throw new ScraperException("Property `" + property + "` not found on page.");

        } catch (URISyntaxException e) {
            throw new ScraperException("URI syntax problem for result `" + result + "`, for extraction array: " + Arrays.toString(extractionArray));
        }
    }

    private void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize(); ) {
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
