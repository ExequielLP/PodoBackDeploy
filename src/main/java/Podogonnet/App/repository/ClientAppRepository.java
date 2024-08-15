package Podogonnet.App.repository;

import Podogonnet.App.entity.ClientApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientAppRepository extends JpaRepository<ClientApp,Long> {
    Optional<ClientApp>findByClientId(String clientId);
}
