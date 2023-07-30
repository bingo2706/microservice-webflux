package com.tanthanh.profileservice;

import com.google.gson.Gson;
import com.tanthanh.commonservice.utils.Constant;
import com.tanthanh.profileservice.data.Profile;
import com.tanthanh.profileservice.model.ProfileDTO;
import com.tanthanh.profileservice.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "application.properties")
@AutoConfigureWebTestClient
@Slf4j
class intergrationTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ProfileRepository profileRepository;

    Profile profile;
    ProfileDTO profileDTO;

    Gson gson = new Gson();
    @BeforeEach
    void setUp(){
        profile = new Profile(1,"test@gmail.com","name", Constant.STATUS_PROFILE_ACTIVE,"CUSTOMER");
        profileDTO = new ProfileDTO(1,"test@gmail.com",Constant.STATUS_PROFILE_ACTIVE,200,"name","CUSTOMER");
    }
    @Test
    void ShouldGetAllProfile(){
        when(profileRepository.findAll()).thenReturn(Flux.just(profile));
        webTestClient.get().uri("/api/v1/profiles")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Profile.class)
                .hasSize(1)
                .consumeWith(response -> {
                    List<Profile> profiles = response.getResponseBody();
                    log.info("List profile "+profiles.toString());
                    Assertions.assertNotNull(profiles);
                    Assertions.assertEquals(profile.getEmail(),profiles.get(0).getEmail());
                });
    }
    @Test
    void GetAllProfileWithEmptyList(){
        when(profileRepository.findAll()).thenReturn(Flux.empty());
        webTestClient.get().uri("/api/v1/profiles")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("PF01")
                .jsonPath("$.message").isEqualTo("Empty profile list !")
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
    }
    @Test
    void CheckDuplicateProfileWithDuplicate(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.just(profile));
        webTestClient.get().uri("/api/v1/profiles/checkDuplicate/test@gmail.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEqualTo(true);
    }
    @Test
    void CheckDuplicateProfileWithNotDuplicate(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        webTestClient.get().uri("/api/v1/profiles/checkDuplicate/test@gmail.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEqualTo(false);
    }
    @Test
    void ShouldCreateNewProfile(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(profileRepository.save(any(Profile.class))).thenReturn(Mono.just(profile));
        webTestClient.post().uri("/api/v1/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(profileDTO), ProfileDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(profileDTO.getEmail())
                .jsonPath("$.id").isEqualTo(profileDTO.getId())
                .jsonPath("$.status").isEqualTo(profileDTO.getStatus())
                .jsonPath("$.name").isEqualTo(profileDTO.getName())
                .jsonPath("$.role").isEqualTo(profileDTO.getRole());

    }
    @Test
    void CreateNewProfileWithDuplicate(){
        when(profileRepository.findByEmail(anyString())).thenReturn(Mono.just(profile));
        webTestClient.post().uri("/api/v1/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(profileDTO),ProfileDTO.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("PF02")
                .jsonPath("$.message").isEqualTo("Duplicate profile !")
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");

    }
}
