package Podogonnet.App.dto;

import lombok.Data;

@Data
public class ServicioPodoDto {
    private String id;
    private String nombre;
    private String descripcion;
    private double costo;
    private ImagenDto imagen;
    private boolean estado;
}
