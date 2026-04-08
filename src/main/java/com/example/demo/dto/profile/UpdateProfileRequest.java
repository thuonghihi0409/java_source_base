package com.example.demo.dto.profile;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 150)
    private String fullName;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;

    @Size(max = 160)
    private String headline;

    @Size(max = 2000)
    private String bio;
}
