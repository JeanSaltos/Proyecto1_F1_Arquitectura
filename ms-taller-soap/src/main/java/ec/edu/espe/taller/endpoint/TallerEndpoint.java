package ec.edu.espe.taller.endpoint;

import ec.edu.espe.taller.anticorruption.OrdenMantenimiento;
import ec.edu.espe.taller.anticorruption.TallerTranslator;
import ec.edu.espe.taller.anticorruption.VehiculoTaller;
import ec.edu.espe.taller.service.ITallerService;
import ec.edu.espe.taller.ws.ConsultarVehiculoRequest;
import ec.edu.espe.taller.ws.ConsultarVehiculoResponse;
import ec.edu.espe.taller.ws.RegistrarOrdenMantenimientoRequest;
import ec.edu.espe.taller.ws.RegistrarOrdenMantenimientoResponse;
import ec.edu.espe.taller.ws.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Endpoint SOAP del Taller Externo.
 *
 * Este endpoint expone exclusivamente operaciones SOAP definidas en el XSD.
 * Utiliza la Capa Anticorrupción (ACL) a través del ITallerService y el
 * TallerTranslator para mantener el dominio interno aislado del contrato SOAP.
 *
 * Operaciones:
 * - consultarVehiculo: Consulta datos de un vehículo por matrícula
 * - registrarOrdenMantenimiento: Registra una incidencia/orden de mantenimiento
 */
@Endpoint
public class TallerEndpoint {

    private static final String NAMESPACE_URI = "http://espe.edu.ec/taller/ws";

    private final ITallerService tallerService;

    @Autowired
    public TallerEndpoint(ITallerService tallerService) {
        this.tallerService = tallerService;
    }

    /**
     * Operación SOAP: consultarVehiculo
     * Recibe una matrícula y retorna los datos del vehículo desde el repositorio del taller.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ConsultarVehiculoRequest")
    @ResponsePayload
    public ConsultarVehiculoResponse consultarVehiculo(@RequestPayload ConsultarVehiculoRequest request) {
        ConsultarVehiculoResponse response = new ConsultarVehiculoResponse();

        // 1. Delegar al servicio de dominio (opera con modelos internos)
        VehiculoTaller vehiculoInterno = tallerService.consultarVehiculo(request.getMatricula());

        if (vehiculoInterno != null) {
            // 2. ACL: Traducir del modelo interno al tipo SOAP/JAXB para la respuesta
            Vehiculo vehiculoSoap = TallerTranslator.toSoap(vehiculoInterno);
            response.setVehiculo(vehiculoSoap);
        } else {
            // Vehículo no encontrado: retornar objeto con indicador
            Vehiculo empty = new Vehiculo();
            empty.setMatricula("NO_ENCONTRADO");
            empty.setMarca("N/A");
            empty.setModelo("N/A");
            empty.setAnio(0);
            response.setVehiculo(empty);
        }

        return response;
    }

    /**
     * Operación SOAP: registrarOrdenMantenimiento
     * Registra una nueva orden de mantenimiento para un vehículo dado.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RegistrarOrdenMantenimientoRequest")
    @ResponsePayload
    public RegistrarOrdenMantenimientoResponse registrarOrden(
            @RequestPayload RegistrarOrdenMantenimientoRequest request) {

        RegistrarOrdenMantenimientoResponse response = new RegistrarOrdenMantenimientoResponse();

        // 1. Delegar al servicio de dominio
        OrdenMantenimiento orden = tallerService.registrarOrden(
                request.getMatricula(),
                request.getDescripcion()
        );

        // 2. Mapear resultado al tipo de respuesta SOAP
        response.setMensaje("Orden de mantenimiento registrada exitosamente para vehículo " + orden.getMatriculaVehiculo());
        response.setIdOrden(orden.getIdOrden());

        return response;
    }
}
