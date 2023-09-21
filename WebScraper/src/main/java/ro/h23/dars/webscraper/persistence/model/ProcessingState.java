/*
 * Copyright (C) 2017 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details. 
 */
package ro.h23.dars.webscraper.persistence.model;

public enum ProcessingState {
    WAITING, PROCESSING, PROCESSED, INVALID, SENT, UNKNOWN;
}
