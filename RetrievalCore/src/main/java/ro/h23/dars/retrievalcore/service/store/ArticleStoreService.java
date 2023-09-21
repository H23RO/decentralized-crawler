package ro.h23.dars.retrievalcore.service.store;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.h23.dars.retrievalcore.persistence.model.*;
import ro.h23.dars.retrievalcore.persistence.repository.ArticleRepository;
import ro.h23.dars.retrievalcore.persistence.repository.PageRepository;
import ro.h23.dars.retrievalcore.persistence.repository.PageUserRepository;
import ro.h23.dars.retrievalcore.api.scraperapi.dto.ArticleInfoDto;
import ro.h23.dars.retrievalcore.service.HashService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ArticleStoreService {

    private static final Logger logger = LogManager.getLogger(ArticleStoreService.class);

    @Value("${dars.store-path}")
    private String storePath;

    private final HashService hashService;

    private final ArticleRepository articleRepository;

    private final PageRepository pageRepository;

    private final PageUserRepository pageUserRepository;

    public ArticleStoreService(HashService hashService, ArticleRepository articleRepository, PageRepository pageRepository, PageUserRepository pageUserRepository) {
        this.hashService = hashService;
        this.articleRepository = articleRepository;
        this.pageRepository = pageRepository;
        this.pageUserRepository = pageUserRepository;
    }

    public void store(Long userId, Site site, ArticleInfoDto articleInfoDto, byte[] featuredImage) throws ArticleStoreServiceException {
        //
        // convert the ArticleInfoDto object to Article
        String contentsHash = articleInfoDto.getContentsTextOnlyHash();
        String featuredImageHash = articleInfoDto.getFeaturedImageHash();

        if (!contentsHash.equals(hashService.computeHash(articleInfoDto.getContentsTextOnly()))) {
            throw new ArticleStoreServiceException("Hashes do not match for article contents: receivedHash: " + contentsHash + " - computedHash: " + hashService.computeHash(articleInfoDto.getContentsTextOnly()));
        }

        if (featuredImageHash != null && featuredImage != null) {
            if (!featuredImageHash.equals(hashService.computeHash(featuredImage))) {
                throw new ArticleStoreServiceException("Hashes do not match for featured image: " + featuredImageHash + " - " + hashService.computeHash(featuredImage));
            }
        }
        if (featuredImageHash == null && featuredImage != null) {
            throw new ArticleStoreServiceException("FeaturedImageHash is null but featuredImage has data");
        }
        if (featuredImageHash != null && featuredImage == null) {
            logger.warn("FeaturedImageHash is not null, but the featured image is null for url: " + articleInfoDto.getUrl()+". Probably the extracted image is invalid");
            featuredImageHash = null;
        }

        // save the hashes for that article-user pair

        Page page = pageRepository.findPageByUrlPath(articleInfoDto.getUrl());
        PageUser pageUser = pageUserRepository.findByPageAndUserNative(page.getId(), userId);
        pageUser.setContentsHash(contentsHash);
        pageUser.setFeaturedImageHash(featuredImageHash);
        pageUser.setState(ProcessingState.COMPLETE);
        pageUserRepository.save(pageUser);
        //System.exit(1);

        Article article = articleRepository.findFirstByUrl(articleInfoDto.getUrl());
        // TODO improve this
        if (article == null) {
            article = new Article();
            article.setExtractedDate(articleInfoDto.getExtractedDate());
            article.setPublishDate(articleInfoDto.getPublishDate());
            article.setSite(site);
            article.setContentsHash(contentsHash);
            article.setFeaturedImageHash(featuredImageHash);
            article.setAuthor(articleInfoDto.getAuthor());
            article.setTitle(articleInfoDto.getTitle());
            article.setUrl(articleInfoDto.getUrl());
            article.setState(ArticleState.NEW);
        } else {
            // TODO support multiple scrappers
            // FIXME throw new ArticleStoreServiceException("Existing articles not supported yet.");
        }
        articleRepository.save(article);

        // access the article directory
        File articleDir = new File(storePath, article.getId() + "");
        articleDir.mkdirs();

        // save the article
        try {
            FileUtils.writeStringToFile(new File(articleDir, "contents_" + contentsHash), articleInfoDto.getContentsTextOnly(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ArticleStoreServiceException("Could not store article contents for articleId: " + article.getId());
        }
        try {
            FileUtils.writeStringToFile(new File(articleDir, "contents_full_" + contentsHash), articleInfoDto.getContents(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ArticleStoreServiceException("Could not store article contents full for articleId: " + article.getId());
        }

        if (featuredImage != null) {
            //System.out.println(featuredImage.length);
            try {
                FileUtils.writeByteArrayToFile(new File(articleDir, "featured_image_" + featuredImageHash), featuredImage);
            } catch (IOException e) {
                throw new ArticleStoreServiceException("Could not store article contents for articleId: " + article.getId());
            }
        //} else {
        //    System.out.println("featured image is null");

        }
        article.setState(ArticleState.VERIFIED);
        articleRepository.save(article);
    }
}
