package ec.edu.espe.taller.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"matricula", "descripcion"})
@XmlRootElement(name = "registrarOrdenMantenimientoRequest", namespace = SoapTypes.NAMESPACE)
public class RegistrarOrdenMantenimientoRequest {

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String matricula;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String descripcion;

    public RegistrarOrdenMantenimientoRequest() {
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
