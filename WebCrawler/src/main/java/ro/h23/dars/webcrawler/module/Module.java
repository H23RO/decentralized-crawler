/*
 * Copyright (C) 2018 Adrian Alexandrescu. All rights reserved.
 * ADRIAN ALEXANDRESCU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * See <license.txt> for more details. 
 */
package ro.h23.dars.webcrawler.module;

/**
 * @author Adrian
 * @version 1.0
 */
public interface Module<I, O> {

    O process(I input) throws ModuleException;
    
    void close() throws ModuleException;
}
