package Podogonnet.App.dto;

import Podogonnet.App.entity.Dia;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Usuario;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
@Data
public class TurnoDto {

    private String id;

    private String nombreServicio;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean estado;
    private boolean turnoSuspendible;
    private boolean feriado;
    private double costo;

}
