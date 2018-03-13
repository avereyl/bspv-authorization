package org.bspv.authorization.process;

import java.util.List;
import java.util.UUID;

import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserProcessService {


    public Page<User> findUsers(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    public User findUser(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    public User saveUser(User user, User principal) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ServiceGrantedAuthority> findUserAuthorities(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

}
