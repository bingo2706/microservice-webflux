package com.tanthanh.profileservice.model;


import com.tanthanh.profileservice.data.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private long id;
    private String email;
    private String status;
    private double initialBalance;
    private String name;
    private String role;

    public static Profile dtoToEntity(ProfileDTO profileDTO){
        Profile profile = new Profile();
        profile.setId(profileDTO.getId());
        profile.setName(profileDTO.getName());
        profile.setRole(profileDTO.getRole());
        profile.setStatus(profileDTO.getStatus());
        profile.setEmail(profileDTO.getEmail());
        return profile;
    }
    public static ProfileDTO entityToDto(Profile profile){
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(profile.getId());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setStatus(profile.getStatus());
        profileDTO.setName(profile.getName());
        profileDTO.setRole(profile.getRole());
        return profileDTO;
    }
}
