package Podogonnet.App.repository;

import Podogonnet.App.entity.ServicioPodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PodoRepository extends JpaRepository<ServicioPodo,String> {

}
