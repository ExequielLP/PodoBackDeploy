package Podogonnet.App.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Turno extends Auditable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @ManyToOne
    @ToString.Exclude
    private ServicioPodo servicioPodo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean estado;
    @ManyToOne
    @ToString.Exclude
    private Usuario usuario;
    @ManyToOne
    private Dia dia;
    private boolean turnoSuspendible;
    private boolean feriado;


}