package com.tanthanh.profileservice.service;

import com.tanthanh.profileservice.model.ProfileDTO;
import com.tanthanh.profileservice.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    public Flux<ProfileDTO> getAllProfile(){
        return profileRepository.findAll()
                .map(profile -> ProfileDTO.entityToDto(profile))
                .switchIfEmpty(Mono.error(new Exception("Profile list empty")));
    }
}
