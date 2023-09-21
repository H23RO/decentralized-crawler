package ro.h23.dars.retrievalcore.service.store;

import org.apache.commons.io.FileUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.io.TikaInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import ro.h23.dars.retrievalcore.config.model.WebResource;
import ro.h23.dars.retrievalcore.persistence.model.Article;
import ro.h23.dars.retrievalcore.persistence.model.Site;
import ro.h23.dars.retrievalcore.persistence.repository.ArticleRepository;
import ro.h23.dars.retrievalcore.persistence.repository.SiteRepository;
import ro.h23.dars.retrievalcore.persistence.specification.ArticleSpecifications;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ArticleRetrieveService {

    private static final int DEFAULT_PAGE_SIZE = 25;

    private static final Logger logger = LoggerFactory.getLogger(ArticleRetrieveService.class);

    @Value("${dars.store-path}")
    private String storePath;

    private final SimpleDateFormat simpleDateFormat;

    private final ArticleRepository articleRepository;

    private final SiteRepository siteRepository;

    public ArticleRetrieveService(ArticleRepository articleRepository, SiteRepository siteRepository) {
        this.articleRepository = articleRepository;
        this.siteRepository = siteRepository;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public List<Article> retrieveArticlesByCriteria(LinkedMultiValueMap<String, String> criteriaMap) throws ArticleRetrieveServiceException {
        //articleRepository.fin
        //ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll().

        // date, publisher, author, category, pageNo, perPage/size, title, orderByDate (asc / desc)
        int page = -1;
        int size = -1;
        Date dateStart = null;
        Date dateEnd = null;
        List<Specification<Article>> specificationList = new ArrayList<>();
        String sortBy = null;
        Sort.Direction sortDirection = null;
        for (Map.Entry<String, List<String>> entry : criteriaMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if (values == null || values.size() == 0) {
                // ignore the key
                continue;
            }
            String singleValue = values.get(0);
            if (singleValue == null || singleValue.isEmpty()) {
                throw new ArticleRetrieveServiceException("Query string `" + key + "` has no value");
            }
            if (values.size() > 1) {
                throw new ArticleRetrieveServiceException("Multiple values for query string `" + key + "` are not supported");
            }
            if (key.equals("category")) {
                throw new ArticleRetrieveServiceException("Query string `" + key + "` is not supported");
            } else if (key.equals("pageNo")) {
                try {
                    page = Integer.parseInt(singleValue);
                } catch(NumberFormatException e) {
                    throw new ArticleRetrieveServiceException("Value for query string `" + key + "` is not a number");
                }
            } else if (key.equals("size") || key.equals("perPage")) {
                try {
                    size = Integer.parseInt(singleValue);
                    if (size > 100) {
                        throw new ArticleRetrieveServiceException("Page size cannot be greater than 100");
                    }
                } catch(NumberFormatException e) {
                    throw new ArticleRetrieveServiceException("Value for query string `" + key + "` is not a number");
                }
            } else if (key.equals("author")) {
                specificationList.add(ArticleSpecifications.hasPropertyValue("author",  singleValue));
            } else if (key.equals("title")) {
                String[] titleValues = singleValue.split(" ");
                for (String titleValue : titleValues) {
                    //specificationList.add(ArticleSpecifications.hasLikePropertyValue("title",  titleValue));
                    titleValue = titleValue.toLowerCase();
                    titleValue = titleValue.replace("â", "[aâ]").replace("ă", "[aă]").replace("î", "[iî]").replace("ș", "[sș]").replace("ț", "[tț]");
                    titleValue = titleValue.replace("a", "[aâă]").replace("i", "[iî]").replace("s", "[sș]").replace("t", "[tț]");
                    specificationList.add(ArticleSpecifications.hasRegExPropertyValue("title",  "(^.*[^a-zA-Z]|^)" + titleValue + "[^a-zA-Z].*$"));
                }
            } else if (key.equals("date")) {
                try {
                    dateStart = simpleDateFormat.parse(singleValue);
                    //System.out.println(dateStart.toString());
                    long time = dateStart.getTime() + 1000 * 60 * 60 * 24 - 1;
                    dateEnd = new Date(time);
                    //System.out.println(dateEnd.toString());
                } catch (ParseException e) {
                    throw new ArticleRetrieveServiceException("Invalid date value for query string `" + key + "`. Value must be in yyyy-MM-dd format and the received value is `" + singleValue + "`");
                }
                //specificationList.add(ArticleSpecifications.hasPropertyValue("extractedDate",  singleValue));
            } else if (key.equals("dateStart")) {
                try {
                    dateStart = simpleDateFormat.parse(singleValue);
                } catch (ParseException e) {
                    throw new ArticleRetrieveServiceException("Invalid date value for query string `" + key + "`. Value must be in yyyy-MM-dd format and the received value is `" + singleValue + "`");
                }
            } else if (key.equals("dateEnd")) {
                try {
                    dateEnd = simpleDateFormat.parse(singleValue);
                    long time = dateEnd.getTime() + 1000 * 60 * 60 * 24 - 1;
                    dateEnd = new Date(time);
                } catch (ParseException e) {
                    throw new ArticleRetrieveServiceException("Invalid date value for query string `" + key + "`. Value must be in yyyy-MM-dd format and the received value is `" + singleValue + "`");
                }
            } else if (key.equals("publisher")) {
                Site site = siteRepository.findOneByName(singleValue);
                // FIXME extracted of publish date ?
                specificationList.add(ArticleSpecifications.hasSite(site));
            } else if (key.equals("orderByDate")) {
                sortBy = "extractedDate";
                if (singleValue.equals("asc")) {
                    sortDirection = Sort.Direction.ASC;
                } else if (singleValue.equals("desc")) {
                    sortDirection = Sort.Direction.DESC;
                } else {
                    throw new ArticleRetrieveServiceException("Invalid date value for query string `" + key + "`. Possible values are `asc` and `desc`, but received `" + singleValue + "`");
                }

            } else {
                throw new ArticleRetrieveServiceException("Invalid key `" + key + "`");
            }
            if (dateStart != null && dateEnd != null) {
                specificationList.add(ArticleSpecifications.isBetweenDates("extractedDate",  dateStart, dateEnd));
            }
        }
        if (page == -1) {
            page = 0;
        }
        if (size == -1) {
            size = DEFAULT_PAGE_SIZE;
        }

        Specification<Article> finalSpecification = null;
        if (specificationList.size() > 0) {
            for (Specification<Article> specification : specificationList) {
                if (finalSpecification == null) {
                    finalSpecification = Specification.where(specificationList.get(0));
                } else {
                    finalSpecification = finalSpecification.and(specification);
                }
            }
        }
        //logger.info("Searched articles for: " + ((finalSpecification != null)?finalSpecification.toString():null));
        // System.out.println(specificationList.size());
        PageRequest pageRequest;
        if (sortBy != null) {
            pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        } else {
            pageRequest = PageRequest.of(page, size);
        }
        return articleRepository.findAll(finalSpecification, pageRequest).toList();
    }

    public Article retrieveArticleById(Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.orElse(null);
    }

    public WebResource retrieveArticleFeaturedImageById(Long id, String featuredImageHash) {
        if (featuredImageHash == null) {
            return null;
        }
        File articleDir = new File(storePath, id + "");
        File file = new File(articleDir, "featured_image_" + featuredImageHash);
        byte[] featuredImageByteArray = null;
        try {
            featuredImageByteArray = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            logger.error("Featured image not found for id: " + id, e);
            return null;
        }
        // Do the detection. Use DefaultDetector / getDetector() for more advanced detection
        Metadata metadata = new Metadata();
        //
        try {
            TikaConfig config = TikaConfig.getDefaultConfig();
            MediaType mediaType = config.getMimeRepository().detect(TikaInputStream.get(featuredImageByteArray), metadata);
            return new WebResource(featuredImageByteArray, mediaType.toString());
        } catch (IOException e) {
            logger.error("Could not determine image type for id: " + id, e);
            return null;
        }
    }

    public WebResource retrieveArticleContentsById(Long id, String contentsHash, boolean isContentsFull) {
        if (contentsHash == null) {
            return null;
        }
        File articleDir = new File(storePath, id + "");
        File file = new File(articleDir, "contents_" + (isContentsFull?"full_":"") + contentsHash);
        try {
            byte[] byteArray = FileUtils.readFileToByteArray(file);
            return new WebResource(byteArray, isContentsFull?"text/html":"text/plain");
        } catch (IOException e) {
            //throw new RuntimeException(e);
            logger.error("Contents text only not found for id: " + id, e);
            return null;
        }
    }
}
