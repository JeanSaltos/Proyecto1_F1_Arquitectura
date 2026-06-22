package ec.edu.espe.taller.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vehiculoType", propOrder = {
        "matricula",
        "marca",
        "modelo",
        "anio",
        "estadoMantenimiento",
        "encontrado"
})
@XmlRootElement(name = "vehiculo", namespace = SoapTypes.NAMESPACE)
public class VehiculoSoap {

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String matricula;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String marca;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String modelo;

    @XmlElement(namespace = SoapTypes.NAMESPACE)
    private int anio;

    @XmlElement(namespace = SoapTypes.NAMESPACE, required = true)
    private String estadoMantenimiento;

    @XmlElement(namespace = SoapTypes.NAMESPACE)
    private boolean encontrado;

    public VehiculoSoap() {
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getEstadoMantenimiento() {
        return estadoMantenimiento;
    }

    public void setEstadoMantenimiento(String estadoMantenimiento) {
        this.estadoMantenimiento = estadoMantenimiento;
    }

    public boolean isEncontrado() {
        return encontrado;
    }

    public void setEncontrado(boolean encontrado) {
        this.encontrado = encontrado;
    }
}
