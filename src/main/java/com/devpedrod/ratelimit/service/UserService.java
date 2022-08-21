package com.devpedrod.ratelimit.service;

import com.devpedrod.ratelimit.domain.User;
import com.devpedrod.ratelimit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void create(User user){
        log.info("New user created, id: {}", user.getUsername());
        userRepository.save(user);
    }

    public User getById(Long id) {
        log.info("Find user by id {}", id);
        return userRepository.findById(id).get();
    }
}
