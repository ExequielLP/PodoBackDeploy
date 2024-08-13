package Podogonnet.App.repository;

import Podogonnet.App.entity.ClientApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientAppRepository extends JpaRepository<ClientApp,String> {
    Optional<ClientApp>findByClientId(String clientId);
}
