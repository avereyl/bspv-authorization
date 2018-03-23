package org.bspv.authorization.process;

import java.util.List;
import java.util.UUID;

import org.bspv.authorization.business.UserBusinessService;
import org.bspv.authorization.model.User;
import org.bspv.authorization.model.wrapper.UserSearchWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserProcessService {
    
    @Autowired
    private UserBusinessService userBusinessService;


    public Page<User> findUsers(PageRequest pageRequest) {
        return userBusinessService.findUsers(pageRequest);
    }
    
    public List<User> findUsers(UserSearchWrapper userSearchWrapper) {
        // TODO Auto-generated method stub
        return null;
    }
    public Page<User> findUsers(UserSearchWrapper userSearchWrapper, PageRequest pageRequest) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @PreAuthorize("hasAuthority('ADMIN') or principal.uuid = uuid")
    public User findUser(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    public User saveUser(User user) {
        // TODO Auto-generated method stub
        return null;
    }



}
