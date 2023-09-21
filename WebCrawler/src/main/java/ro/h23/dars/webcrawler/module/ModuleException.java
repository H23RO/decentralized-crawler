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
public class ModuleException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public ModuleException() {
    }

    /**
     * @param message
     */
    public ModuleException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ModuleException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ModuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
