package Podogonnet.App.servis.auth;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String id;
    private String userName;
    private String email;
    private String rol;
    private String jwt;
}
