/*
 * Copyright (C) 2017 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details.
 */
package ro.h23.dars.webcrawler.finder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.h23.dars.webcrawler.util.URLUtils;

/**
 * @author Adrian
 * @version 1.0
 */
public class LinkFinder implements Finder<String> {

    private final Logger         log                     = LoggerFactory.getLogger(getClass());

    //Pattern.DOTALL -> . matches all including new lines
    private static final Pattern A_TAG_PATTERN           = Pattern.compile("<a\\b[^>]*href=[\"\'][^>]*>(.*?)</a>", Pattern.DOTALL);
    private static final Pattern HREF_ATTRIBUTE_PATTERN  = Pattern.compile("href=[\"\'][^>]*?[\"\']", Pattern.DOTALL);                 // changed from "href=\"[^>]*\">"
    private static final Pattern BUTTON_TAG_PATTERN      = Pattern.compile("<button\\b[^>]*value=[\"\'][^>]*>(.*?)</button>",
            Pattern.DOTALL);
    private static final Pattern VALUE_ATTRIBUTE_PATTERN = Pattern.compile("value=[\"\'][^>]*?[\"\']", Pattern.DOTALL);                // changed from "href=\"[^>]*\">"

    private final String               url;
    private Matcher              aTagMatcher;
    private Matcher              buttonTagMatcher;
    private String               base;

    /**
     * 
     */
    public LinkFinder(String url, String contents) {
        this.url = url;
        this.aTagMatcher = A_TAG_PATTERN.matcher(contents);
        this.buttonTagMatcher = BUTTON_TAG_PATTERN.matcher(contents);
        int index = contents.indexOf("<base");
        if (index != -1) {
            Matcher matcher = Pattern.compile(".*<base\\b[^>]*href=[\"\']([^>]*)[\"\'].*", Pattern.DOTALL).matcher(contents);
            this.base = (matcher.find()) ? matcher.replaceFirst("$1") : null;
        } else {
            this.base = null;
        }
    }

    /**
     * 
     * @return the next absolute URL in the contents; null, if there are no more valid URLs
     */
    public String next() {
        //TODO impove this
        while (aTagMatcher.find()) {
            Matcher matcher = HREF_ATTRIBUTE_PATTERN.matcher(aTagMatcher.group());
            if (!matcher.find()) {
                log.warn("No href attribute found for: " + aTagMatcher.group());
                continue;
            }
            //System.out.println(">>> matcher.group(): "+ matcher.group());
            /*
             * String link = matcher.group().replaceFirst("href=\"", "").replaceFirst("\">", "")
             * .replaceFirst("\"[\\s]?target=\"[a-zA-Z_0-9]*", "");
             */
            String link = matcher.group().replaceFirst(".*href=[\"\']([^\"]*)[\"\'].*", "$1");
            link = link.replaceAll("&amp;", "&");
            //System.out.println(">>> link: " + link+"; "+base);
            if (URLUtils.isValidLink(link)) {
                return URLUtils.getAbsoluteURLString(url, link, base);
            }
        }
        while (buttonTagMatcher.find()) {
            Matcher matcher = VALUE_ATTRIBUTE_PATTERN.matcher(buttonTagMatcher.group());
            if (!matcher.find()) {
                log.warn("No href attribute found for: " + buttonTagMatcher.group());
                continue;
            }
            //System.out.println(">>> matcher.group(): "+ matcher.group());
            /*
             * String link = matcher.group().replaceFirst("href=\"", "").replaceFirst("\">", "")
             * .replaceFirst("\"[\\s]?target=\"[a-zA-Z_0-9]*", "");
             */
            String link = matcher.group().replaceFirst(".*value=[\"\']([^\"]*)[\"\'].*", "$1");
            link = link.replaceAll("&amp;", "&");
            //System.out.println(">>> link: " + link+"; "+base);
            if (URLUtils.isValidLink(link)) {
                return URLUtils.getAbsoluteURLString(url, link, base);
            }
        }
        return null;
    }

}
