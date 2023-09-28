package ro.h23.dars.retrievalcore.api.publicapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.h23.dars.retrievalcore.api.publicapi.dto.ArticleResponseDto;
import ro.h23.dars.retrievalcore.api.publicapi.dto.ArticleResponsesDto;
import ro.h23.dars.retrievalcore.persistence.model.Article;
import ro.h23.dars.retrievalcore.service.GenericExternalApiService;

import java.util.List;
import java.util.Map;

public class RetrievalCoreApiService extends GenericExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(RetrievalCoreApiService.class);

    public RetrievalCoreApiService(String serverBaseUrl) {
        //super(retrievalCoreApiConfig.getServer(), retrievalCoreApiConfig.getAuthenticationPath(), "{\"username\": \"" + retrievalCoreApiConfig.getUsername() + "\", \"password\": \"" + retrievalCoreApiConfig.getPassword() + "\"}");
        super(serverBaseUrl, null, null);
    }


    public List<ArticleResponseDto> getArticleUrls(LinkedMultiValueMap<String, String> requestParamMap) {
            //return sendRequest(HttpMethod.GET, "/api/all-public/"+serverId+"/articles", requestParamMap, ArticleResponsesDto.class);
            StringBuilder queryParamString = new StringBuilder();
            for (Map.Entry<String, List<String>> e : requestParamMap.entrySet()) {
                for (String v : e.getValue()) {
                    if (queryParamString.length() != 0) {
                        queryParamString.append("&");
                    }
                    queryParamString.append(e.getKey()).append("=").append(v);
                }
            }
            logger.info("Obtaining getArticleUrls - queryParamString: " + queryParamString);
            return sendRequest(HttpMethod.GET, "/api/public/articles?" + queryParamString.toString(), null, ArticleResponsesDto.class, true);
    }

    public ArticleResponseDto getArticleById(Long id) {
        return sendRequest(HttpMethod.GET, "/api/public/articles/"+ id, null, ArticleResponseDto.class, true);
    }

    public ResponseEntity<byte[]> getArticleFeaturedImageById(Long id) {
        // TODO improve this and add the Content-Type header
        return ResponseEntity.ok()
                .body(sendRequest(HttpMethod.GET, "/api/public/articles/"+ id + "/featured-image", null, ByteArrayResource.class, true).getByteArray());
    }

    public ResponseEntity<byte[]> getArticleContents(Long id) {
        // TODO improve this and add the Content-Type header
        return ResponseEntity.ok()
                .body(sendRequest(HttpMethod.GET, "/api/public/articles/"+ id + "/contents", null, ByteArrayResource.class, true).getByteArray());
    }

    public ResponseEntity<byte[]> getArticleContentsById(Long id) {
        // TODO improve this and add the Content-Type header
        return ResponseEntity.ok()
                .body(sendRequest(HttpMethod.GET, "/api/public/articles/"+ id + "/contents-full", null, ByteArrayResource.class, true).getByteArray());
    }

}
