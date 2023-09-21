package ro.h23.dars.webscraper.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public interface HashService {

    String computeHash(String data);
    String computeHash(byte[] data);

    String computeHash(InputStream data) throws IOException;
}
