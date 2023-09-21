package ro.h23.dars.webscraper.service;

import org.apache.commons.io.FileUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.data.WebResource;

import java.io.File;
import java.io.IOException;

@Service
public class WebResourceStoreService {

    public void store(String outputDirectory, String fileName, WebResource webResource) throws IOException, MimeTypeException {
        // Load your Tika config, find all the Tika classes etc
        TikaConfig config = TikaConfig.getDefaultConfig();

        // Do the detection. Use DefaultDetector / getDetector() for more advanced detection
        Metadata metadata = new Metadata();
        metadata.add("Content-Type", webResource.getMediaType());
        MediaType mediaType = config.getMimeRepository().detect(TikaInputStream.get(webResource.getContents()), metadata);

        // Fest the most common extension for the detected type
        MimeType mimeType = config.getMimeRepository().forName(mediaType.toString());
        String extension = mimeType.getExtension();

        FileUtils.writeByteArrayToFile(new File(outputDirectory, fileName + extension), webResource.getContents());
    }
}
