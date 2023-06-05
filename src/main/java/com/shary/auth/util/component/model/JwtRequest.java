package com.shary.auth.util.component.model;

import lombok.Data;

@Data
public class JwtRequest {
    private String email;
    private String phoneNumber;
    private String password;
}
