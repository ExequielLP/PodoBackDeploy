package Podogonnet.App.servis;

import Podogonnet.App.entity.Imagen;
import Podogonnet.App.repository.ImagenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@Service
public class ImagenServicio {
@Autowired
private ImagenRepo imagenRepo;

    public Imagen crearImagen(MultipartFile file) throws IOException {
        Imagen imagen=new Imagen();
        imagen.setContent(file.getBytes());
        imagen.setMime(file.getContentType());
        imagen.setState(true);
        imagen.setName(file.getOriginalFilename());
        imagenRepo.save(imagen);
        return imagen;
    }
}
