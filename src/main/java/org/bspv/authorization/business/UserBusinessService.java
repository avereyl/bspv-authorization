package org.bspv.authorization.business;

import org.bspv.authorization.model.User;
import org.bspv.authorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserBusinessService {
    
    @Autowired
    private UserRepository userRepository;

    public Page<User> findUsers(PageRequest pageRequest) {
        return userRepository.findAllUsers(pageRequest);
    }

}
