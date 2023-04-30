package com.tanthanh.profileservice.service;

import com.tanthanh.commonservice.common.CommonException;
import com.tanthanh.profileservice.model.ProfileDTO;
import com.tanthanh.profileservice.repository.ProfileRepository;
import com.tanthanh.profileservice.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
                .map(ProfileDTO::entityToDto)
                .switchIfEmpty(Mono.error(new Exception("Profile list empty")));
    }
    public Mono<Boolean> checkDuplicate(String email){
        return profileRepository.findByEmail(email)
                .flatMap(profile -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }
    public Mono<ProfileDTO> createNewProfile(ProfileDTO profileDTO){
        return checkDuplicate(profileDTO.getEmail())
                .flatMap(aBoolean -> {
                    if(Boolean.TRUE.equals(aBoolean)){
                        return Mono.error(new CommonException("PF02","Duplicate profile !", HttpStatus.BAD_REQUEST));
                    }else{
                        profileDTO.setStatus(Constant.STATUS_PROFILE_PENDING);
                        return createProfile(profileDTO);
                    }
                });
    }
    public Mono<ProfileDTO> createProfile(ProfileDTO profileDTO){
        return Mono.just(profileDTO)
                .map(ProfileDTO::dtoToEntity)
                .flatMap(profile -> profileRepository.save(profile))
                .map(ProfileDTO::entityToDto)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doOnSuccess(profileDTO1 -> {
                });
    }
}
