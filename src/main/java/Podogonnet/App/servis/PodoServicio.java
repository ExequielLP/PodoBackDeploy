package Podogonnet.App.servis;

import Podogonnet.App.entity.Imagen;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.repository.ImagenRepo;
import Podogonnet.App.repository.PodoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            podoRepository.save(servicioPodoDb);
            return servicioPodoDb;
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return null;
        }


    }

    public List<ServicioPodo> listaServicios() {
        try {
            return podoRepository.findAll();
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
