package com.devpedrod.ratelimit.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimitService {
    Map<String, Bucket> bucketMap = new HashMap<>();

    public String getAuthenticatedUser(){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails){
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    public Bucket getBucket() {
        var bucket = bucketMap.getOrDefault(getAuthenticatedUser(),
                Bucket4j.builder()
                        .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
                        .build());
        bucketMap.put(getAuthenticatedUser(), bucket);
        return bucket;
    }
}
