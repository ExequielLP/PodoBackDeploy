package Podogonnet.App.servis;


import Podogonnet.App.entity.ClientApp;
import Podogonnet.App.mapper.ClientAppMapper;
import Podogonnet.App.repository.ClientAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterClientService implements RegisteredClientRepository {
    @Autowired
    private ClientAppRepository clientAppRepository;

    @Override
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    public RegisteredClient findById(String id) {
      ClientApp clientApp=clientAppRepository.findByClientId(id).orElseThrow(()->new UsernameNotFoundException("cliente Not Found"));
        return ClientAppMapper.toRegisteredClient(clientApp);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return this.findById(clientId);
    }
}

