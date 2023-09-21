/*
 * Copyright (C) 2017 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details. 
 */
package ro.h23.dars.webcrawler.finder;

/**
 * @author Adrian
 * @version 1.0
 */
public interface Finder<T> {

    T next();
}
