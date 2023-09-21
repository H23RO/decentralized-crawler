package ro.h23.dars.retrievalcore.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.h23.dars.retrievalcore.auth.persistence.model.RoleType;
import ro.h23.dars.retrievalcore.auth.persistence.model.User;
import ro.h23.dars.retrievalcore.auth.persistence.repository.RoleRepository;
import ro.h23.dars.retrievalcore.auth.persistence.repository.UserRepository;
import ro.h23.dars.retrievalcore.persistence.model.Page;
import ro.h23.dars.retrievalcore.persistence.model.PageUser;
import ro.h23.dars.retrievalcore.persistence.model.PageUserId;
import ro.h23.dars.retrievalcore.persistence.model.ProcessingState;
import ro.h23.dars.retrievalcore.persistence.repository.PageUserRepository;

import java.util.List;
import java.util.Random;

@Service
public class NewPageAddedServiceImpl implements  NewPageAddedService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PageUserRepository pageUserRepository;

    public NewPageAddedServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PageUserRepository pageUserRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.pageUserRepository = pageUserRepository;
    }

    @Override
    public void process(Page page) {
        //Role role = roleRepository.findByName(RoleType.ROLE_SCRAPER).get();
        //Set<Role> roleSet = new HashSet<>();
        //roleSet.add(role);
        //List<User> userList = userRepository.findByRolesIn(roleSet, Pageable.unpaged());
        List<User> userList = userRepository.findByRolesName(RoleType.ROLE_SCRAPER, Pageable.unpaged());

        // allocate page to a random scraper
        User user = userList.get(new Random().nextInt(userList.size()));
        PageUser pageUser = new PageUser();
        pageUser.setPage(page);
        pageUser.setUser(user);
        pageUser.setState(ProcessingState.NEW);
        pageUser.setId(new PageUserId(page.getId(), user.getId()));
        pageUserRepository.save(pageUser);

        // allocate page to all scrappers
        /*
        userList.forEach(user -> {
            PageUser pageUser = new PageUser();
            pageUser.setPage(page);
            pageUser.setUser(user);
            pageUser.setState(ProcessingState.NEW);
            pageUser.setId(new PageUserId(page.getId(), user.getId()));
            pageUserRepository.save(pageUser);
        });
        */

    }
}
