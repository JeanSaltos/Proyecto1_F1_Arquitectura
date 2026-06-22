package ec.edu.espe.taller.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"vehiculo"})
@XmlRootElement(name = "consultarVehiculoResponse", namespace = SoapTypes.NAMESPACE)
public class ConsultarVehiculoResponse {

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private VehiculoSoap vehiculo;

    public ConsultarVehiculoResponse() {
    }

    public VehiculoSoap getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoSoap vehiculo) {
        this.vehiculo = vehiculo;
    }
}
