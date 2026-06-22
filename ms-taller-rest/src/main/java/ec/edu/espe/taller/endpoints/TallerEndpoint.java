package ec.edu.espe.taller.endpoints;

import ec.edu.espe.taller.anticorruption.TallerSoapMapper;
import ec.edu.espe.taller.models.OrdenMantenimiento;
import ec.edu.espe.taller.models.VehiculoTaller;
import ec.edu.espe.taller.service.ITallerService;
import ec.edu.espe.taller.ws.ConsultarVehiculoRequest;
import ec.edu.espe.taller.ws.ConsultarVehiculoResponse;
import ec.edu.espe.taller.ws.RegistrarOrdenMantenimientoRequest;
import ec.edu.espe.taller.ws.RegistrarOrdenMantenimientoResponse;
import ec.edu.espe.taller.ws.SoapTypes;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class TallerEndpoint {

    private final ITallerService tallerService;
    private final TallerSoapMapper mapper;

    public TallerEndpoint(ITallerService tallerService, TallerSoapMapper mapper) {
        this.tallerService = tallerService;
        this.mapper = mapper;
    }

    @PayloadRoot(namespace = SoapTypes.NAMESPACE, localPart = "consultarVehiculoRequest")
    @ResponsePayload
    public ConsultarVehiculoResponse consultarVehiculo(@RequestPayload ConsultarVehiculoRequest request) {
        VehiculoTaller vehiculo = tallerService.consultarVehiculo(request.getMatricula());
        return mapper.toConsultarVehiculoResponse(request.getMatricula(), vehiculo);
    }

    @PayloadRoot(namespace = SoapTypes.NAMESPACE, localPart = "registrarOrdenMantenimientoRequest")
    @ResponsePayload
    public RegistrarOrdenMantenimientoResponse registrarOrdenMantenimiento(
            @RequestPayload RegistrarOrdenMantenimientoRequest request) {
        VehiculoTaller vehiculo = tallerService.consultarVehiculo(request.getMatricula());
        if (vehiculo == null) {
            return mapper.toVehiculoNoEncontradoResponse(request.getMatricula());
        }

        OrdenMantenimiento orden = tallerService.registrarOrden(
                request.getMatricula(),
                request.getDescripcion()
        );
        return mapper.toOrdenRegistradaResponse(orden);
    }
}
