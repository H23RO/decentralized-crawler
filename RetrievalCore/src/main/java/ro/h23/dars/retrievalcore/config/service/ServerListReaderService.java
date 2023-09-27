package ro.h23.dars.retrievalcore.config.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.h23.dars.retrievalcore.config.exception.ServerListReaderException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServerListReaderService {

    @Value("${dars.config.server-list-path}")
    private String serverListPath;

    public ServerListReaderService() {

    }

    public List<String> read() throws ServerListReaderException {
        //File siteInfoDir = new File(ClassLoader.getSystemResource(crawlerConfig.getSiteInfoPath()).getFile());
        File serverListFile = new File(serverListPath);
        if (serverListFile.exists()) {
            List<String> serverList = new ArrayList<>();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(serverListFile));
                String server;
                while((server = br.readLine()) != null) {
                    serverList.add(server);
                }
                //logger.info("Loading serverList: " + serverSet.toString());
                return serverList;
            } catch (IOException e) {
                throw new ServerListReaderException("Error while reading server list from file `" + serverListPath + "`", e);
            }
        } else {
            throw new ServerListReaderException("There is no file named `" + serverListPath + "` that contains the server list");
        }

    }

}
