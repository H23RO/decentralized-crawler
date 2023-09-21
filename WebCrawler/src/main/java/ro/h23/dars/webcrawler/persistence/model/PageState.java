/*
 * Copyright (C) 2017 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details. 
 */
package ro.h23.dars.webcrawler.persistence.model;

/**
 * @author Adrian
 * @created 9 ian. 2017
 * @version 1.0
 */
public enum PageState {
    WAITING, PROCESSING, PROCESSED, INVALID, SENT;
}
