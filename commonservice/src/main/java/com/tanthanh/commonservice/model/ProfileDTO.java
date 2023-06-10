package com.tanthanh.commonservice.model;
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
}
