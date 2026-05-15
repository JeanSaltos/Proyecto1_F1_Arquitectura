package ec.edu.espe.taller.models;

/**
 * Modelo interno del dominio de Taller para órdenes de mantenimiento.
 * Parte de la Capa Anticorrupción — independiente de los tipos SOAP.
 */
public class OrdenMantenimiento {

    private String idOrden;
    private String matriculaVehiculo;
    private String descripcion;
    private String estado; // REGISTRADA, EN_PROCESO, COMPLETADA
    private String fechaRegistro;

    public OrdenMantenimiento() {
    }

    public OrdenMantenimiento(String idOrden, String matriculaVehiculo, String descripcion, String estado, String fechaRegistro) {
        this.idOrden = idOrden;
        this.matriculaVehiculo = matriculaVehiculo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public String getMatriculaVehiculo() { return matriculaVehiculo; }
    public void setMatriculaVehiculo(String matriculaVehiculo) { this.matriculaVehiculo = matriculaVehiculo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return "OrdenMantenimiento{" +
                "idOrden='" + idOrden + '\'' +
                ", matriculaVehiculo='" + matriculaVehiculo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaRegistro='" + fechaRegistro + '\'' +
                '}';
    }
}
