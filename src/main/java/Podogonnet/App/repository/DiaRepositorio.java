package Podogonnet.App.repository;

import Podogonnet.App.entity.Dia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaRepositorio extends JpaRepository<Dia,String> {


 Optional<Dia> findByFecha(LocalDate date);
}
