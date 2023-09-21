package ro.h23.dars.webcrawler.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.h23.dars.webcrawler.dto.PageDto;
import ro.h23.dars.webcrawler.dto.PagesDto;
import ro.h23.dars.webcrawler.persistence.model.Page;
import ro.h23.dars.webcrawler.persistence.model.PageState;
import ro.h23.dars.webcrawler.persistence.repository.PageRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PageSendToRetrievalCoreService {


    private final RetrievalCoreApiService retrievalCoreApiService;

    private final SiteDataConverterService siteDataConverterService;

    private final PageRepository pageRepository;

    public PageSendToRetrievalCoreService(RetrievalCoreApiService retrievalCoreApiService, SiteDataConverterService siteDataConverterService, PageRepository pageRepository) {
        this.retrievalCoreApiService = retrievalCoreApiService;
        this.siteDataConverterService = siteDataConverterService;
        this.pageRepository = pageRepository;
    }

    public void start() {
        while(true) {
            List<Page> pageList = pageRepository.findAllArticlesThatAreNotSent(Pageable.ofSize(100));
            if (pageList.size() > 0) {
                PagesDto pagesDto = new PagesDto();
                for (Page page : pageList) {
                    String siteName = page.getSite().getName();
                    List<PageDto> pageDtoList = pagesDto.get(siteName);
                    if (pageDtoList == null) {
                        pageDtoList = new ArrayList<>();
                        pagesDto.put(siteName, pageDtoList);
                    }
                    pageDtoList.add(siteDataConverterService.pageToPageDto(page));
                    page.setState(PageState.SENT);
                }
                try {
                    String result = retrievalCoreApiService.sendPages(pagesDto);
                    if (result == null || !result.equals("ok")) {
                        throw new RuntimeException("Something went wrong when sending pages to the RetrievalCore");
                    }
                    pageRepository.saveAll(pageList);
                } catch (Exception e) {
                    // FIXME
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
