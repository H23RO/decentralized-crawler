package ro.h23.dars.retrievalcore.api.publicapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.h23.dars.retrievalcore.api.publicapi.service.RetrievalCoreApiService;
import ro.h23.dars.retrievalcore.config.model.WebResource;
import ro.h23.dars.retrievalcore.service.store.ArticleRetrieveService;
import ro.h23.dars.retrievalcore.service.store.ArticleRetrieveServiceException;
import ro.h23.dars.retrievalcore.persistence.model.Article;
import ro.h23.dars.retrievalcore.api.publicapi.dto.ArticleResponseDto;
import ro.h23.dars.retrievalcore.api.publicapi.service.ArticleForTransportConverterService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/all-public/{serverid}")
public class AllPublicController extends ArticleResponseDto {

    private static final Logger logger = LoggerFactory.getLogger(AllPublicController.class);

    private List<String> serverList = new ArrayList<>();
    private List<RetrievalCoreApiService> retrievalCoreApiServiceList = new ArrayList<>();

    private final PublicController publicController;

    public AllPublicController(PublicController publicController) {
        this.publicController = publicController;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
        for(String server : serverList) {
            retrievalCoreApiServiceList.add(new RetrievalCoreApiService(server));
        }
    }

    @GetMapping("articles")
    public List<ArticleResponseDto> getArticleUrls(@PathVariable String serverid, @RequestParam LinkedMultiValueMap<String, String> requestParamMap) {
        logger.info(requestParamMap.toString());
        try {
            int serverId = Integer.parseInt(serverid);
            RetrievalCoreApiService retrievalCoreApiService = retrievalCoreApiServiceList.get(serverId);
            return retrievalCoreApiService.getArticleUrls(requestParamMap);
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid serverId");
        }
    }

    @GetMapping("articles/{id}")
    public ArticleResponseDto getArticleById(@PathVariable String serverid, @PathVariable Long id) {
        try {
            int serverId = Integer.parseInt(serverid);
            RetrievalCoreApiService retrievalCoreApiService = retrievalCoreApiServiceList.get(serverId);
            return retrievalCoreApiService.getArticleById(id);
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid serverId");
        }
    }

    @GetMapping("articles/{id}/featured-image")
    public ResponseEntity<byte[]> getArticleFeaturedImageById(@PathVariable String serverid, @PathVariable Long id) {
        try {
            int serverId = Integer.parseInt(serverid);
            RetrievalCoreApiService retrievalCoreApiService = retrievalCoreApiServiceList.get(serverId);
            return retrievalCoreApiService.getArticleFeaturedImageById(id);
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid serverId");
        }
    }

    @GetMapping("articles/{id}/contents")
    public ResponseEntity<byte[]> getArticleContents(@PathVariable String serverid, @PathVariable Long id) {
        try {
            int serverId = Integer.parseInt(serverid);
            RetrievalCoreApiService retrievalCoreApiService = retrievalCoreApiServiceList.get(serverId);
            return retrievalCoreApiService.getArticleContents(id);
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid serverId");
        }
    }

    @GetMapping("articles/{id}/contents-full")
    public ResponseEntity<byte[]> getArticleContentsById(@PathVariable String serverid, @PathVariable Long id) {
        try {
            int serverId = Integer.parseInt(serverid);
            RetrievalCoreApiService retrievalCoreApiService = retrievalCoreApiServiceList.get(serverId);
            return retrievalCoreApiService.getArticleContentsById(id);
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid serverId");
        }
    }

}
