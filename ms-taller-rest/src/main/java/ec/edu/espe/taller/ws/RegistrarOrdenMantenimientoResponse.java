package ec.edu.espe.taller.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"idOrden", "matricula", "estado", "fechaRegistro", "mensaje"})
@XmlRootElement(name = "registrarOrdenMantenimientoResponse", namespace = SoapTypes.NAMESPACE)
public class RegistrarOrdenMantenimientoResponse {

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String idOrden;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String matricula;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String estado;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String fechaRegistro;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String mensaje;

    public RegistrarOrdenMantenimientoResponse() {
    }

    public String getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(String idOrden) {
        this.idOrden = idOrden;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
