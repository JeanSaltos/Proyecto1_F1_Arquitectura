package ec.edu.espe.taller.service.impl;

import ec.edu.espe.taller.models.OrdenMantenimiento;
import ec.edu.espe.taller.models.VehiculoTaller;
import ec.edu.espe.taller.repository.TallerRepository;
import ec.edu.espe.taller.service.ITallerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TallerServiceImpl implements ITallerService {

    private final TallerRepository tallerRepository;

    public TallerServiceImpl(TallerRepository tallerRepository) {
        this.tallerRepository = tallerRepository;
    }

    @Override
    public VehiculoTaller consultarVehiculo(String matricula) {
        return tallerRepository.findVehiculo(matricula);
    }

    @Override
    public OrdenMantenimiento registrarOrden(String matricula, String descripcion) {
        String idOrden = tallerRepository.registrarOrden(matricula, descripcion);
        return new OrdenMantenimiento(idOrden, matricula, descripcion, "REGISTRADA", LocalDateTime.now().toString());
    }
}
