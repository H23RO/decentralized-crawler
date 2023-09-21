package ro.h23.dars.retrievalcore.api.publicapi.service;

import org.springframework.stereotype.Service;
import ro.h23.dars.retrievalcore.api.publicapi.dto.ArticleResponseDto;
import ro.h23.dars.retrievalcore.persistence.model.Article;

@Service
public class ArticleForTransportConverterService {

    public ArticleResponseDto convertArticleToArticleResponseDto(Article article) {
        if (article == null) {
            return null;
        }
        ArticleResponseDto articleResponseDto = new ArticleResponseDto();
        articleResponseDto.setId(article.getId());
        articleResponseDto.setTitle(article.getTitle());
        articleResponseDto.setAuthor(article.getAuthor());
        articleResponseDto.setPublishDate(article.getExtractedDate()); // FIXME
        articleResponseDto.setExtractedDate(article.getExtractedDate());
        articleResponseDto.setLastUpdated(article.getUpdateTimestamp());
        articleResponseDto.setPublisher(article.getSite().getName());
        articleResponseDto.setUrl(article.getUrl());
        articleResponseDto.setContentsHash(article.getContentsHash());
        return articleResponseDto;
    }

}
