package com.devpedrod.ratelimit.service;

import com.devpedrod.ratelimit.domain.User;
import com.devpedrod.ratelimit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void create(User user){
        log.info("New user created, id: {}", user.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User getById(Long id) {
        log.info("Find user by id {}", id);
        return userRepository.findById(id).get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByUsername(username)
                .orElseThrow(() ->  new UsernameNotFoundException(String.format("user with username %s not found", username)));
    }
}
