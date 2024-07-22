package Podogonnet.App.dto.auth;

import lombok.Data;

@Data
public class AutheticationRequest {
    private String userName;
    private String password;
}
