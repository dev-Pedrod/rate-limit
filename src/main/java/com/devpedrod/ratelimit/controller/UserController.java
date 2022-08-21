package com.devpedrod.ratelimit.controller;

import com.devpedrod.ratelimit.domain.User;
import com.devpedrod.ratelimit.service.RateLimitService;
import com.devpedrod.ratelimit.service.UserService;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RateLimitService rateLimit;

    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody User user){
        userService.create(user);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri()).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        ConsumptionProbe probe = rateLimit.getBucket().tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()){
            var user = userService.getById(id);
            return ResponseEntity.ok()
                    .header("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()))
                    .body(user);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-Rate-Limit-Retry-After-Seconds",
                            String.valueOf(TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill())))
                    .build();
        }
    }
}
