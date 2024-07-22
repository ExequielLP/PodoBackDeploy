package Podogonnet.App.repository;

import Podogonnet.App.entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenRepo  extends JpaRepository<Imagen,String> {
}
