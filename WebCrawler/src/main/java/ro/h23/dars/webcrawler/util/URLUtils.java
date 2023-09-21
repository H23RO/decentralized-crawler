/*
 * Copyright (C) 2017 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details.
 */
package ro.h23.dars.webcrawler.util;

/**
 * @author Adrian
 * @created 13 ian. 2017
 * @version 1.0
 */
public class URLUtils {

    private URLUtils() {
    }

    public static boolean isValidLink(String link) {
        if (link.matches("mailto:.*|javascript:.*") || (link.length() > 13 && link.indexOf("://", 10) != -1) || link.contains("/tel:+")) {
            return false;
        } else {
            return true;
        }
    }
    
    public static String getURLWithoutBase(String url) {
        return url.replaceAll("http[s]?://[^/]*/(.*)", "$1");
    }

    public static String getAbsoluteURLString(String url, String link, String base) {
        try {
            link = link.trim();
            if (link.equals("") || link.equals("#")) {
                return url;
            }
            link = link.split("#")[0];

            //System.err.println(url+"; "+ link);
            if (link.matches("http[s]?://.*")) {
                return link;
            }
            // whatsapp://
            if (link.contains("://")) {
                return link;
            }
            if (base != null && base.startsWith("http")) {
                url = base;
            }
            if (link.equals("")) {
                return url;
            }
            // the link starts with //
            if (link.startsWith("//")) {
                return url.split(":")[0] + ":// "+ link;
            }
            if (link.matches("/.*")) {
                //System.out.println(">>> " + url+"; " + link+"; "+base+"; ");
                // get the base url (schema, host, port)
                //FIXME improve
                //TODO does not properly work for https://www.redgoblin.ro/boardgames/?p=2
                // return url.replaceAll("(http[s]?://.*)/.*", "$1") + link;
                int x = url.indexOf("/", 10);
                //System.out.println(">>> " + x +";  " + url.substring(0, x));
                return url.substring(0, x) + link;
            }
            // url ends with a /
            if (url.matches(".*/")) {
                return url + link;
            } else {
                // remove characters after last / from url
                return url.replaceAll("(.*)/[^/]*", "$1") + "/" + link;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("Cannot get absolute url string for url: `" + url + "`; link: `" + link + "`; base: `" + base + "`",e);
        }
        /*
         * // the link starts with / and the url does not end with /
         * if (link.matches("/.*") && url.matches(".*$[^/]")) {
         * return url + "/" + link;
         * }
         * // the link does not start with / and the url
         * if (link.matches("[^/].*") && url.matches(".*[^/]")) {
         * return url + "/" + link;
         * }
         * // the link starts with / and the url does not end with /
         * if (link.matches("/.*") && url.matches(".*[/]")) {
         * return url + link;
         * }
         * // the link does not start with / and the url does not end with /
         * if (link.matches("/.*") && url.matches(".*[^/]")) {
         * return url + link;
         * }
         */

       // throw new RuntimeException("Cannot make the link absolute. Url: " + url + " Link " + link);
    }
}
