package Podogonnet.App.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnosUsuario {
    private String id;
    private String nombreServicio;
    private double costo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean estado;
    private String nombreUsuario;


}
