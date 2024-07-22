package Podogonnet.App.servis.auth;

import Podogonnet.App.dto.RegisterUser;
import Podogonnet.App.dto.SaveUser;
import Podogonnet.App.dto.auth.AutheticationRequest;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AutheticateService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private JwtService jwtService;

    public RegisterUser registerOneCostumer(SaveUser newUser) throws Exception {
        Usuario user= usuarioServicio.registerOneCostumer(newUser);
        RegisterUser userDto=new RegisterUser();
        userDto.setName(user.getNombre());
        userDto.setUserName(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRol().name());
        userDto.setId(user.getId());

        String jwt=jwtService.generateToken(user,generateExtraClaims(user));
        userDto.setJwt(jwt);

        return userDto;
    }

    private Map<String, Object> generateExtraClaims(Usuario user) {
        Map<String,Object>extraClaims=new HashMap<>();
        extraClaims.put("name",user.getNombre());
        extraClaims.put("role",user.getRol().name());
        extraClaims.put("authorizaties",user.getAuthorities());
        extraClaims.put("userName",user.getUsername());
        return extraClaims;

    }

    public AuthenticationResponse login(AutheticationRequest authen) {
        Authentication authentication=new UsernamePasswordAuthenticationToken(authen.getUserName(),authen.getPassword());


        authenticationManager.authenticate(authentication);

       UserDetails user= usuarioServicio.findOneByUsername(authen.getUserName());

       String jwt=jwtService.generateToken(user,generateExtraClaims((Usuario) user));

        AuthenticationResponse authenticationResponse=new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        authenticationResponse.setUserName(((Usuario) user).getNombre());
        authenticationResponse.setRol(((Usuario) user).getRol().toString());
        authenticationResponse.setId(((Usuario) user).getId());



return authenticationResponse;

    }

    public boolean validateToken(String jwt) {
        try {
            jwtService.extracUsername(jwt);
            return true;

        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("validateTkoken(stringjwt");
            return false;

        }


    }

    public Usuario findLogginInUser() {
        /*aca basicamente tengo que obtener el usuarui del securutyContextHolder*/
        UsernamePasswordAuthenticationToken auth= (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String user= (String) auth.getPrincipal();
        return usuarioServicio.findOneByUsername(user);

    }

    public AuthenticationResponse validateGetProfile(String jwt) {
        try {
            String username=jwtService.extracUsername(jwt);
            Usuario usuario=usuarioServicio.findOneByUsername(username);
            AuthenticationResponse authenticationResponse=new AuthenticationResponse();
            authenticationResponse.setId(usuario.getId());
            authenticationResponse.setUserName(usuario.getUsername());
            authenticationResponse.setRol(String.valueOf(usuario.getRol()));
            authenticationResponse.setJwt(jwt);
            return authenticationResponse;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}

