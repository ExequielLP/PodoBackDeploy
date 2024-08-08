package Podogonnet.App.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class TurnosUsuario {
    private String id;
    private String nombreServicio;
    private double costo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
