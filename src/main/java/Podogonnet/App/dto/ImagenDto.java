package Podogonnet.App.dto;

import lombok.Data;

@Data
public class ImagenDto {
    private String id;
    private String mime;
    private String name;
    private Boolean state;
    private byte[] content;
}
