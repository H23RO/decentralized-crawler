package ro.h23.dars.webscraper.service;

import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.common.service.JsonConverterService;
import ro.h23.dars.webscraper.config.ScraperConfig;
import ro.h23.dars.webscraper.data.ArticleInfo;
import ro.h23.dars.webscraper.exception.ArticleInfoSaverException;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class ArticleInfoSaverService {

    private final JsonConverterService jsonConverterService;

    public ArticleInfoSaverService(JsonConverterService jsonConverterService, ScraperConfig scraperConfig) {
        this.jsonConverterService = jsonConverterService;
    }
    public void save(String outputDirString, int articleId, ArticleInfo articleInfo) throws ArticleInfoSaverException {
        File outputDir = new File(outputDirString);
        outputDir.mkdirs();
        File file = new File(outputDir, articleId + ".json");
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(jsonConverterService.toJson(articleInfo).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ArticleInfoSaverException(e);
        }
    }
}
