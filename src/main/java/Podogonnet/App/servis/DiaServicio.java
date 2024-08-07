package Podogonnet.App.servis;

import Podogonnet.App.entity.Dia;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.repository.DiaRepositorio;
import Podogonnet.App.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DiaServicio {
    @Autowired
    private DiaRepositorio diaRepositorio;
    @Autowired
    private TurnoRepository turnoRepositorio;

    public Dia turnosDelDia(LocalDate localDate) throws Exception {
        try {
            System.out.println("andetro de pubkuc dua turnosDeLdIA");
            // Utilizamos el método definido en el repositorio para obtener los turnos por fecha
            Optional<Dia> diaRespuesta = diaRepositorio.findByFecha(localDate);
            System.out.println(diaRespuesta);
            System.out.println("SALIENDO de pubkuc dua turnosDeLdIA");
            if (diaRespuesta.isPresent()) {
                Dia dia = diaRespuesta.get();
                // Devolvemos el objeto Dia obtenido del repositorio
                return dia;
            } else {
                throw new RuntimeException("No se encontraron turnos para la fecha especificada.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error al buscar los turnos para el día: " + e.getMessage(), e);
        }
    }

    }



