package Podogonnet.App.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String clientSecret;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> clientAuthenticationMethods;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorizationGrantTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> redirectUris;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> scopes;

    private int durationInMinutes;

    private boolean requiredProofKey;



}
