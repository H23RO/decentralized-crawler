package ro.h23.dars.webscraper.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

@Service
public class JsonConverterService {

    private final Gson gson         = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();

    public String toJson(Object o) {
        return gson.toJson(o);
    }
    public <T> T fromJson(String jsonString, Class<T> clasz) {
        return gson.fromJson(jsonString, clasz);
    }


}
