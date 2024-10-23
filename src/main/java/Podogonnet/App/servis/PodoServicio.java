package Podogonnet.App.servis;

import Podogonnet.App.dto.ServicioPodoDto;
import Podogonnet.App.dto.auth.ImagenDto;
import Podogonnet.App.entity.Imagen;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.repository.PodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.standard.expression.Each;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PodoServicio {

    @Autowired
    private PodoRepository podoRepository;
    @Autowired
    private ImagenServicio imagenServicio;


    public List<ServicioPodo> findAll() {

        return podoRepository.findAll();

    }

    @Transactional
    public ServicioPodo crearServicioPodo(String nombre, String descripcion, double costo, MultipartFile file) throws Exception {

        try {
            ServicioPodo servicioPodoDb = new ServicioPodo();
            Imagen imagen = imagenServicio.crearImagen(file);
            servicioPodoDb.setNombre(nombre);
            servicioPodoDb.setDescripcion(descripcion);
            servicioPodoDb.setCosto(costo);
            servicioPodoDb.setImagen(imagen);
            servicioPodoDb.setEstado(true);
            podoRepository.save(servicioPodoDb);
            return servicioPodoDb;
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return null;
        }


    }

    public  List<ServicioPodoDto> listaServicios() {
        try {
            List<ServicioPodo> servicioPodoDB = podoRepository.findAll();
            List<ServicioPodoDto> listservicioPodoDto = new ArrayList<>();


            for (ServicioPodo aux : servicioPodoDB) {
                ServicioPodoDto servicioPodoDto = new ServicioPodoDto();
                ImagenDto imagenDto=new ImagenDto();
                servicioPodoDto.setNombre(aux.getNombre());
                servicioPodoDto.setDescripcion(aux.getDescripcion());
                servicioPodoDto.setCosto(aux.getCosto());
                imagenDto.setContent(aux.getImagen().getContent());
                imagenDto.setMime(aux.getImagen().getMime());
                imagenDto.setName(aux.getImagen().getName());
                imagenDto.setState(aux.getImagen().getState());
                servicioPodoDto.setImagen(imagenDto);
                servicioPodoDto.setEstado(aux.isEstado());
                listservicioPodoDto.add(servicioPodoDto);
            }
            return listservicioPodoDto;
        } catch (Exception e) {

            System.out.println(e.getStackTrace());
            return null;
        }


    }

    public ServicioPodo findById(String id) {

        return podoRepository.findById(id).orElseThrow(null);

    }

    public void AltaBaja(String id) {
        Optional<ServicioPodo> servicioPodoOptional = podoRepository.findById(id);
        if (servicioPodoOptional.isPresent()) {
            ServicioPodo servicioPodo = servicioPodoOptional.get();
            servicioPodo.setEstado(!servicioPodo.isEstado());
            podoRepository.save(servicioPodo);
        }
    }

    public void modificarServicio(String id, String nombre, String descripcion, double costo, MultipartFile file) throws IOException {
        Optional<ServicioPodo> servicio = podoRepository.findById(id);
        Imagen imagen = imagenServicio.crearImagen(file);
        if (servicio.isPresent()) {
            ServicioPodo servicioModifar = servicio.get();
            servicioModifar.setNombre(nombre);
            servicioModifar.setCosto(costo);
            servicioModifar.setDescripcion(descripcion);
            servicioModifar.setImagen(imagen);
            servicioModifar.setEstado(servicioModifar.isEstado());
            podoRepository.save(servicioModifar);
        }
    }
}
