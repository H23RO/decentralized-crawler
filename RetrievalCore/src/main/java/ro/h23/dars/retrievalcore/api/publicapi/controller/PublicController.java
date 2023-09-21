package ro.h23.dars.retrievalcore.api.publicapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.h23.dars.retrievalcore.config.model.WebResource;
import ro.h23.dars.retrievalcore.service.store.ArticleRetrieveService;
import ro.h23.dars.retrievalcore.service.store.ArticleRetrieveServiceException;
import ro.h23.dars.retrievalcore.persistence.model.Article;
import ro.h23.dars.retrievalcore.api.publicapi.dto.ArticleResponseDto;
import ro.h23.dars.retrievalcore.api.publicapi.service.ArticleForTransportConverterService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/public")
public class PublicController extends ArticleResponseDto {

    private static final Logger logger = LoggerFactory.getLogger(PublicController.class);

    private final ArticleRetrieveService articleRetrieveService;

    private final ArticleForTransportConverterService articleForTransportConverterService;

    public PublicController(ArticleRetrieveService articleRetrieveService, ArticleForTransportConverterService articleForTransportConverterService) {
        this.articleRetrieveService = articleRetrieveService;
        this.articleForTransportConverterService = articleForTransportConverterService;
    }

    @GetMapping("articles")
    public List<ArticleResponseDto> getArticleUrls(@RequestParam LinkedMultiValueMap<String, String> requestParamMap) {
        logger.info(requestParamMap.toString());
        try {
            List<Article> articleList = articleRetrieveService.retrieveArticlesByCriteria(requestParamMap);
            List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();
            articleList.forEach(article -> articleResponseDtoList.add(articleForTransportConverterService.convertArticleToArticleResponseDto(article)));
            return articleResponseDtoList;
        } catch(ArticleRetrieveServiceException e) {
            logger.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("articles/{id}")
    public ArticleResponseDto getArticleById(@PathVariable Long id) {
        Article article = articleRetrieveService.retrieveArticleById(id);
        return articleForTransportConverterService.convertArticleToArticleResponseDto(article);
    }

    @GetMapping("articles/{id}/featured-image")
    public ResponseEntity<byte[]> getArticleFeaturedImageById(@PathVariable Long id) {
        Article article = articleRetrieveService.retrieveArticleById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        WebResource webResource = articleRetrieveService.retrieveArticleFeaturedImageById(id, article.getFeaturedImageHash());
        if (webResource != null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", webResource.getMediaType());
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(webResource.getContents());

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("articles/{id}/contents")
    public ResponseEntity<byte[]> getArticleContents(@PathVariable Long id) {
        Article article = articleRetrieveService.retrieveArticleById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        WebResource webResource = articleRetrieveService.retrieveArticleContentsById(id, article.getContentsHash(), false);
        if (webResource != null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", webResource.getMediaType());
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(webResource.getContents());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("articles/{id}/contents-full")
    public ResponseEntity<byte[]> getArticleContentsById(@PathVariable Long id) {
        Article article = articleRetrieveService.retrieveArticleById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        WebResource webResource = articleRetrieveService.retrieveArticleContentsById(id, article.getContentsHash(), true);
        if (webResource != null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", webResource.getMediaType());
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(webResource.getContents());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    @GetMapping("/get-image-dynamic-type")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImageDynamicType(@RequestParam("jpg") boolean jpg) {
        MediaType contentType = jpg ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;
        InputStream in = jpg ?
                getClass().getResourceAsStream("/com/baeldung/produceimage/image.jpg") :
                getClass().getResourceAsStream("/com/baeldung/produceimage/image.png");
        return ResponseEntity.ok()
                .contentType(contentType)
                //.contentLength(in.contentLength()) //ByteArrayResource
                .body(new InputStreamResource(in));
    }*/


}
