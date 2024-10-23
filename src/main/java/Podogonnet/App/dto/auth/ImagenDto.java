package Podogonnet.App.dto.auth;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Data
public class ImagenDto {
    private String id;
    private String mime;
    private String name;
    private Boolean state;
    private byte[] content;
}
