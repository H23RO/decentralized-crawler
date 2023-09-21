/*
 * Copyright (C) 2018 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details.
 */
package ro.h23.dars.webcrawler.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetcherModule implements Module<URL, String> {

    //TODO add logic for a sleep between requests
    @Override
    public String process(URL input) throws ModuleException {
        String pageContents;
        BufferedReader reader = null;
        try {

            HttpURLConnection huc = (HttpURLConnection) input.openConnection();
            huc.setConnectTimeout(120000);
            huc.setReadTimeout(120000);
            // added this because on lelegames.ro otherwise the response is 403
            huc.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
            reader = new BufferedReader(new InputStreamReader(huc.getInputStream()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                sb.append(s).append("\r\n");
            }
            pageContents = sb.toString();
        } catch (IOException e) {
            throw new ModuleException("Error reading page contents for URL: " + input, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new ModuleException("Error closing stream", e);
                }
            }
        }
        return pageContents;
    }

    public static void main(String[] args) throws MalformedURLException, ModuleException {
        System.out.println(new FetcherModule().process(new URL("https://www.lelegames.ro/jocuri-de-societate?page=2")));
    }

    /* (non-Javadoc)
     * @see ro.h23.productcrawler.modules.Module#close()
     */
    @Override
    public void close() throws ModuleException {
        
    }

}
