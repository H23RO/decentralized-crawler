package ro.h23.dars.retrievalcore.service;

import ro.h23.dars.retrievalcore.persistence.model.Page;

public interface NewPageAddedService {

    void process(Page page);
}
