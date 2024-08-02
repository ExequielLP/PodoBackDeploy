package Podogonnet.App.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Types;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Imagen {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String mime;
    private String name;
    private Boolean state;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 10485760)
    @JdbcTypeCode(Types.VARBINARY)
    private byte[] content;
}
