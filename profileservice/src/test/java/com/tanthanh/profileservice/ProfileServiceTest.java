package com.tanthanh.profileservice;

import com.tanthanh.commonservice.utils.Constant;
import com.tanthanh.profileservice.data.Profile;
import com.tanthanh.profileservice.event.EventProducer;
import com.tanthanh.profileservice.model.ProfileDTO;
import com.tanthanh.profileservice.repository.ProfileRepository;
import com.tanthanh.profileservice.service.ProfileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
 class ProfileServiceTest {
    @InjectMocks
    ProfileService profileService;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    EventProducer eventProducer;
    Profile profile;
    ProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        when(eventProducer.send(anyString(),anyString())).thenReturn(Mono.just("OK"));
        ReflectionTestUtils.setField(profileService, "profileRepository", profileRepository);
        ReflectionTestUtils.setField(profileService, "eventProducer", eventProducer);
        profileDTO = new ProfileDTO(1,"test@gmail.com", Constant.STATUS_PROFILE_ACTIVE,200,"name","ADMIN");
        profile = new Profile(1,"test@gmail.com","name", Constant.STATUS_PROFILE_ACTIVE,"ADMIN");
    }
    @Test
    void getAllProfile(){
        when(profileRepository.findAll()).thenReturn(Flux.just(profile));
        profileService.getAllProfile().doOnNext(profileDTO1 -> Assertions.assertEquals(profileDTO,profileDTO1)).subscribe();

        when(profileRepository.findAll()).thenReturn(Flux.empty());
        profileService.getAllProfile().doOnNext(Assertions::assertNotNull).subscribe();
    }
    @Test
    void checkDuplicate(){
        when(profileRepository.findByEmail("test")).thenReturn(Mono.just(profile));
        profileService.checkDuplicate("test").doOnNext(aBoolean -> Assertions.assertEquals(true,aBoolean)).subscribe();

        when(profileRepository.findByEmail("test")).thenReturn(Mono.empty());
        profileService.checkDuplicate("test").doOnNext(aBoolean -> Assertions.assertEquals(false,aBoolean)).subscribe();
    }
    @Test
    void getDetailProfileByEmail(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.just(profile));
        profileService.getDetailProfileByEmail(anyString()).doOnNext(profileDTO1 -> Assertions.assertEquals(profileDTO,profileDTO1)).subscribe();

        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        profileService.getDetailProfileByEmail(anyString()).doOnNext(Assertions::assertNotNull).subscribe();
    }
    @Test
    void updateStatusProfile(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.just(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(Mono.just(profile));
        profileService.updateStatusProfile(profileDTO).doOnNext(profileDTO1 -> Assertions.assertEquals(profileDTO,profileDTO1)).subscribe();

        when(profileRepository.save(any(Profile.class))).thenReturn(Mono.error(new Exception("Some erros")));
        profileService.updateStatusProfile(profileDTO).doOnNext(Assertions::assertNotNull).subscribe();
    }

    @Test
    void createNewProfileWithDuplicateProfile(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.just(profile));
        profileService.createNewProfile(profileDTO).doOnNext(Assertions::assertNotNull).subscribe();
    }
    @Test
    void createNewProfileWithSaveError(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(profileRepository.save(any(Profile.class))).thenReturn(Mono.error(new Exception("Some error")));
        profileService.createNewProfile(profileDTO).doOnNext(Assertions::assertNotNull).subscribe();
    }
    @Test
    void createNewProfileWithStatusPending(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        profile.setStatus(Constant.STATUS_PROFILE_PENDING);
        when(profileRepository.save(any(Profile.class))).thenReturn(Mono.just(profile));
        profileService.createNewProfile(profileDTO)
                .doOnNext(profileDTO1 -> Assertions.assertEquals(profileDTO,profileDTO1)).subscribe();
    }
    @Test
    void createNewProfileWithNotStatusPending(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        profile.setStatus(Constant.STATUS_PROFILE_ACTIVE);
        when(profileRepository.save(any(Profile.class))).thenReturn(Mono.just(profile));
        profileService.createNewProfile(profileDTO).doOnNext(Assertions::assertNotNull).subscribe();
    }
}
