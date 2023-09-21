package ro.h23.dars.webscraper.service;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import ro.h23.dars.webscraper.data.WebResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Service
public class WebResourceRetrieverService {

    public WebResource retrieve(String url) throws IOException {
        return retrieve(new URL(url));
    }

    public WebResource retrieve(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        String mediaType = connection.getContentType();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(connection.getInputStream(), byteArrayOutputStream);
        //BufferedImage originalImage= ImageIO.read(connection.getInputStream());
        // new MimetypesFileTypeMap().getContentType();
        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //ImageIO.write(originalImage, "jpg", byteArrayOutputStream);
        //byteArrayOutputStream.flush();
        return new WebResource(byteArrayOutputStream.toByteArray(), mediaType);
    }

    // TODO??? https://www.baeldung.com/java-download-file
}
