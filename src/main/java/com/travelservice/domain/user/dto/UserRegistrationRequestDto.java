package com.travelservice.domain.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequestDto {
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
}
