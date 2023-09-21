package ro.h23.dars.webcrawler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.h23.dars.webcrawler.config.RetrievalCoreApiConfig;
import ro.h23.dars.webcrawler.dto.PagesDto;
import ro.h23.dars.webcrawler.dto.SiteDto;
import ro.h23.dars.webcrawler.dto.SitesDto;

import java.util.List;

@Service
public class RetrievalCoreApiService extends GenericExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(RetrievalCoreApiService.class);

    public RetrievalCoreApiService(RetrievalCoreApiConfig retrievalCoreApiConfig) {
        super(retrievalCoreApiConfig.getServer(), retrievalCoreApiConfig.getAuthenticationPath(), "{\"username\": \"" + retrievalCoreApiConfig.getUsername() + "\", \"password\": \"" + retrievalCoreApiConfig.getPassword() + "\"}");
    }


    @ResponseBody
    public List<SiteDto> retrieveSites() {
        logger.info("Obtaining sites from RetrievalCore");
        return sendRequest(HttpMethod.GET, "/api/crawler/sites", null, SitesDto.class);
    }

    @ResponseBody
    public String sendPages(PagesDto pagesDto) {
        logger.info("Sending " + pagesDto + " pages to RetrievalCore");
        return sendRequest(HttpMethod.POST, "/api/crawler/pages", pagesDto, String.class);
    }
}
