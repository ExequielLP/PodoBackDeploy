package Podogonnet.App.entity;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedBy
    protected String creadoPor;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaHoraCreacion;

    @LastModifiedBy
    protected String ultimaActualizacionPor;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaHoraUltimaActualizacion;
}
