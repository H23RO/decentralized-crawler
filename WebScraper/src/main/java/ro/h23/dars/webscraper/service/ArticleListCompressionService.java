package ro.h23.dars.webscraper.service;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.persistence.model.Article;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

@Service
public class ArticleListCompressionService {

    private static final Logger logger = LogManager.getLogger(ArticleListCompressionService.class);

    public byte[] compress(String inputDirString, List<Article> articleList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream gzo = new GzipCompressorOutputStream(baos);
        try (ArchiveOutputStream out = new ZipArchiveOutputStream(gzo)) {
            for (Article article : articleList) {
                String templateName = article.getTemplateName();
                Integer articleId = article.getId();
                // process article json
                File f = new File(inputDirString + "/" + templateName, articleId + ".json");
                ArchiveEntry entry = out.createArchiveEntry(f, templateName + "___" + articleId);
                out.putArchiveEntry(entry);
                if (f.isFile()) {
                    IOUtils.copy(Files.newInputStream(f.toPath()), out);
                } else {
                    throw new RuntimeException("File not found: " + f.getAbsolutePath());
                }
                out.closeArchiveEntry();
                // process image
                f = new File(inputDirString + "/" + templateName, articleId + "");
                // TODO remove...  legacy if ...
                if (!f.exists()) {
                    f = new File(inputDirString + "/" + templateName, articleId + ".jpg");
                    if (!f.exists()) {
                        f = new File(inputDirString + "/" + templateName, articleId + ".png");
                        if (!f.exists()) {
                            f = new File(inputDirString + "/" + templateName, articleId + ".gif");
                        }
                    }
                }
                entry = out.createArchiveEntry(f, templateName + "___" + articleId + "___image");
                out.putArchiveEntry(entry);
                if (f.isFile()) {
                    IOUtils.copy(Files.newInputStream(f.toPath()), out);
                } else {
                    //throw new RuntimeException("File not found: " + f.getAbsolutePath());
                    logger.warn("File not found: " + f.getAbsolutePath());
                }
                out.closeArchiveEntry();
            }
            out.finish();
        }
        gzo.flush();

        return baos.toByteArray();
    }
}
