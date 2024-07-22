package Podogonnet.App.dto;

import lombok.Data;

@Data
public class RegisterUser {
    private String id;
    private String userName;
    private String email;
    private String name;
    private String role;
    private String jwt;
}
