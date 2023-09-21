package ro.h23.dars.webscraper.service;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import ro.h23.dars.webscraper.config.RetrievalCoreApiConfig;
import ro.h23.dars.webscraper.dto.ArticleUrlsDto;
import ro.h23.dars.webscraper.dto.ExtractorTemplatesDto;

@Service
public class RetrievalCoreApiService extends GenericExternalApiService {

    public RetrievalCoreApiService(RetrievalCoreApiConfig retrievalCoreApiConfig) {
        super(retrievalCoreApiConfig.getServer(), retrievalCoreApiConfig.getAuthenticationPath(), "{\"username\": \"" + retrievalCoreApiConfig.getUsername() + "\", \"password\": \"" + retrievalCoreApiConfig.getPassword() + "\"}");
    }

    @ResponseBody
    public ExtractorTemplatesDto retrieveExtractorTemplates() {
        return sendRequest(HttpMethod.GET, "/api/scraper/extractor-templates", null, ExtractorTemplatesDto.class);
    }

    @ResponseBody
    public ArticleUrlsDto retrieveArticleUrls() {
        return sendRequest(HttpMethod.GET, "/api/scraper/article-urls?page=0&size=20000", null, ArticleUrlsDto.class);
    }

    @ResponseBody
    public ArticleUrlsDto retrieveArticleUrls(int page, int size) {
        return sendRequest(HttpMethod.GET, "/api/scraper/article-urls?page="+page+"&size="+size, null, ArticleUrlsDto.class);
    }

    @ResponseBody
    public String sendArticleInfos(byte[] data) {
        return sendRequest(HttpMethod.POST, "/api/scraper/articles", data, String.class);
    }
}
