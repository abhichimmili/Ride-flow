
package com.vcube.rideflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserResponseDTO user;
}
