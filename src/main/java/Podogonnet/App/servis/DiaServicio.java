package Podogonnet.App.servis;

import Podogonnet.App.dto.TurnosUsuario;
import Podogonnet.App.entity.Dia;
import Podogonnet.App.entity.Feriado;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.repository.DiaRepositorio;
import Podogonnet.App.repository.FeriadoRepository;
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
    @Autowired
    private FeriadoRepository feriadoRepository;

    public List<TurnosUsuario> turnosDelDia(LocalDate localDate) throws Exception {
        try {
            System.out.println("andetro de pubkuc dua turnosDeLdIA");
            // Utilizamos el método definido en el repositorio para obtener los turnos por
            // fecha
            Optional<Dia> diaRespuesta = diaRepositorio.findByFecha(localDate);
            System.out.println(diaRespuesta);
            System.out.println("SALIENDO de pubkuc dua turnosDeLdIA");
            if (diaRespuesta.isPresent()) {
                Dia dia = diaRespuesta.get();
                System.out.println("*************************");
                System.out.println(dia);
                System.out.println("*************************");
                List<TurnosUsuario> listaturnosDTO = new ArrayList<>();
                for (Turno diaAUX : dia.getTurnos()) {
                    TurnosUsuario turnosDTO = new TurnosUsuario();
                    turnosDTO.setId(diaAUX.getId());
                    turnosDTO.setStartTime(diaAUX.getStartTime());
                    turnosDTO.setEndTime(diaAUX.getEndTime());
                    turnosDTO.setEstado(diaAUX.isEstado());
                    listaturnosDTO.add(turnosDTO);
                }
                System.out.println("---------------------------------------");
                System.out.println(listaturnosDTO);
                // Devolvemos el objeto Dia obtenido del repositorio
                return listaturnosDTO;
            } else {
                throw new RuntimeException("No se encontraron turnos para la fecha especificada.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error al buscar los turnos para el día: " + e.getMessage(), e);
        }
    }

    public void agregarFeriado(Feriado feriadoFront) {
        LocalDate localDate = feriadoFront.getFecha();
        Dia dia=new Dia();
        Optional<Dia>diaDB=diaRepositorio.findByFecha(localDate);
        Feriado feriado=new Feriado();
        feriado.setFecha(feriadoFront.getFecha());
        feriado.setDescripcion(feriadoFront.getDescripcion());
        feriadoRepository.save(feriado);
        if (diaDB.isPresent()){
            dia=diaDB.get();
            dia.setFeriado(feriado);
            diaRepositorio.save(dia);
        }
    }
}
