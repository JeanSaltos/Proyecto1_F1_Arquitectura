package ec.edu.espe.taller.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"matricula"})
@XmlRootElement(name = "consultarVehiculoRequest", namespace = SoapTypes.NAMESPACE)
public class ConsultarVehiculoRequest {

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String matricula;

    public ConsultarVehiculoRequest() {
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
