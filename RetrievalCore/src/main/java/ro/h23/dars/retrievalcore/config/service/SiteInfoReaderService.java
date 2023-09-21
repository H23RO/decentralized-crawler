package ro.h23.dars.retrievalcore.config.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.h23.dars.retrievalcore.config.exception.SiteInfoReaderException;
import ro.h23.dars.retrievalcore.config.model.SiteInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteInfoReaderService {
    @Value("${dars.config.site-info-path}")
    private String siteInfoPath;

    public SiteInfoReaderService() {

    }

    public List<SiteInfo> read() throws SiteInfoReaderException {

        //File siteInfoDir = new File(ClassLoader.getSystemResource(crawlerConfig.getSiteInfoPath()).getFile());
        File siteInfoDir = new File(siteInfoPath);

        File[] fileArray = siteInfoDir.listFiles();
        if (fileArray != null) {
            List<SiteInfo> siteSeedList = new ArrayList<>(fileArray.length);
            ObjectMapper objectMapper = new ObjectMapper();
            for (File f : fileArray) {
                try {
                    SiteInfo siteInfo = objectMapper.readValue(f, SiteInfo.class);
                    siteSeedList.add(siteInfo);
                } catch (IOException e) {
                    throw new SiteInfoReaderException("Invalid JSON for file `" + f.getName() + "`", e);
                }
            }
            return siteSeedList;
        } else {
            throw new SiteInfoReaderException("The path `" + siteInfoDir + "` does not denote a directory");
        }
    }

}
