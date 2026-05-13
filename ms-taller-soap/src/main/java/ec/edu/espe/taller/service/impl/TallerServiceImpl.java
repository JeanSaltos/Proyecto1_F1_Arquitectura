package ec.edu.espe.taller.service.impl;

import ec.edu.espe.taller.anticorruption.OrdenMantenimiento;
import ec.edu.espe.taller.anticorruption.TallerTranslator;
import ec.edu.espe.taller.anticorruption.VehiculoTaller;
import ec.edu.espe.taller.repository.TallerRepository;
import ec.edu.espe.taller.service.ITallerService;
import ec.edu.espe.taller.ws.Vehiculo;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de dominio de Taller.
 * Utiliza la Capa Anticorrupción (TallerTranslator) para traducir
 * entre el repositorio (que usa tipos JAXB) y el dominio interno.
 */
@Service
public class TallerServiceImpl implements ITallerService {

    private final TallerRepository tallerRepository;

    public TallerServiceImpl(TallerRepository tallerRepository) {
        this.tallerRepository = tallerRepository;
    }

    @Override
    public VehiculoTaller consultarVehiculo(String matricula) {
        Vehiculo vehiculoSoap = tallerRepository.findVehiculo(matricula);
        // ACL: Traducir del modelo JAXB externo al modelo interno
        return TallerTranslator.fromSoap(vehiculoSoap);
    }

    @Override
    public OrdenMantenimiento registrarOrden(String matricula, String descripcion) {
        String idOrden = tallerRepository.registrarOrden(matricula, descripcion);
        // ACL: Crear modelo interno desde los datos de la petición
        return TallerTranslator.crearOrdenDesdeRequest(matricula, descripcion, idOrden);
    }
}
