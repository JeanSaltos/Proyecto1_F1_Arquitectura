package ec.edu.espe.taller.models;

/**
 * Modelo interno del dominio de Taller.
 * Esta clase pertenece exclusivamente al Bounded Context de Mantenimiento/Taller
 * y NO depende del esquema XSD ni de las clases generadas por JAXB.
 *
 * La Capa Anticorrupción (ACL) traduce entre este modelo y los tipos SOAP externos.
 */
public class VehiculoTaller {

    private String matricula;
    private String marca;
    private String modelo;
    private int anio;
    private String estadoMantenimiento; // OPERATIVO, EN_REVISION, FUERA_DE_SERVICIO

    public VehiculoTaller() {
    }

    public VehiculoTaller(String matricula, String marca, String modelo, int anio, String estadoMantenimiento) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.estadoMantenimiento = estadoMantenimiento;
    }

    // Getters y Setters
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getEstadoMantenimiento() { return estadoMantenimiento; }
    public void setEstadoMantenimiento(String estadoMantenimiento) { this.estadoMantenimiento = estadoMantenimiento; }

    @Override
    public String toString() {
        return "VehiculoTaller{" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", estadoMantenimiento='" + estadoMantenimiento + '\'' +
                '}';
    }
}
