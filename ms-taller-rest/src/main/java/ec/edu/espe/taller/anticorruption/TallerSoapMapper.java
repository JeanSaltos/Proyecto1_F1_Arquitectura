package ec.edu.espe.taller.anticorruption;

import ec.edu.espe.taller.models.OrdenMantenimiento;
import ec.edu.espe.taller.models.VehiculoTaller;
import ec.edu.espe.taller.ws.ConsultarVehiculoResponse;
import ec.edu.espe.taller.ws.RegistrarOrdenMantenimientoResponse;
import ec.edu.espe.taller.ws.VehiculoSoap;
import org.springframework.stereotype.Component;

@Component
public class TallerSoapMapper {

    public ConsultarVehiculoResponse toConsultarVehiculoResponse(String matricula, VehiculoTaller vehiculo) {
        ConsultarVehiculoResponse response = new ConsultarVehiculoResponse();
        response.setVehiculo(toSoapVehicle(matricula, vehiculo));
        return response;
    }

    public RegistrarOrdenMantenimientoResponse toOrdenRegistradaResponse(OrdenMantenimiento orden) {
        RegistrarOrdenMantenimientoResponse response = new RegistrarOrdenMantenimientoResponse();
        response.setIdOrden(orden.getIdOrden());
        response.setMatricula(orden.getMatriculaVehiculo());
        response.setEstado(orden.getEstado());
        response.setFechaRegistro(orden.getFechaRegistro());
        response.setMensaje("Orden de mantenimiento registrada correctamente");
        return response;
    }

    public RegistrarOrdenMantenimientoResponse toVehiculoNoEncontradoResponse(String matricula) {
        RegistrarOrdenMantenimientoResponse response = new RegistrarOrdenMantenimientoResponse();
        response.setIdOrden("");
        response.setMatricula(matricula);
        response.setEstado("RECHAZADA");
        response.setFechaRegistro("");
        response.setMensaje("No existe vehiculo registrado con matricula " + matricula);
        return response;
    }

    private VehiculoSoap toSoapVehicle(String requestedMatricula, VehiculoTaller vehiculo) {
        VehiculoSoap soapVehicle = new VehiculoSoap();

        if (vehiculo == null) {
            soapVehicle.setMatricula(requestedMatricula);
            soapVehicle.setMarca("NO_ENCONTRADO");
            soapVehicle.setModelo("NO_ENCONTRADO");
            soapVehicle.setAnio(0);
            soapVehicle.setEstadoMantenimiento("NO_ENCONTRADO");
            soapVehicle.setEncontrado(false);
            return soapVehicle;
        }

        soapVehicle.setMatricula(vehiculo.getMatricula());
        soapVehicle.setMarca(vehiculo.getMarca());
        soapVehicle.setModelo(vehiculo.getModelo());
        soapVehicle.setAnio(vehiculo.getAnio());
        soapVehicle.setEstadoMantenimiento(vehiculo.getEstadoMantenimiento());
        soapVehicle.setEncontrado(true);
        return soapVehicle;
    }
}
