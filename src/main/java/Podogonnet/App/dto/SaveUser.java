package Podogonnet.App.dto;

import lombok.Data;

@Data
public class SaveUser {

    private String name;
    private String userName;
    private String email;
    private String password;
    private String repeatePassword;
}
