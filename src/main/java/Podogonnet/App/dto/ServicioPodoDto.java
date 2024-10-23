package Podogonnet.App.dto;

import Podogonnet.App.dto.auth.ImagenDto;
import Podogonnet.App.entity.Imagen;
import lombok.Data;

@Data
public class ServicioPodoDto {
    private String nombre;
    private String descripcion;
    private double costo;
    private ImagenDto imagen;
    private boolean estado;
}
