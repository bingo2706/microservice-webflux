package com.tanthanh.profileservice;

import com.tanthanh.commonservice.utils.Constant;
import com.tanthanh.profileservice.controller.ProfileController;
import com.tanthanh.profileservice.model.ProfileDTO;
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

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProfileControllerTest {
    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private ProfileDTO profileDTO;
    @BeforeEach
    public void setUp(){
        profileDTO = new ProfileDTO(1,"test@gmail.com", Constant.STATUS_PROFILE_ACTIVE,200,"name","ADMIN");
        ReflectionTestUtils.setField(profileController,"profileService",profileService);
    }
    @Test
    void getAllProfileSuccess(){
        when(profileService.getAllProfile()).thenReturn(Flux.just(profileDTO));
        profileController.getAllProfile().getBody().
                doOnNext(profileDTO1 -> Assertions.assertEquals(profileDTO,profileDTO1)).subscribe();
    }
}
