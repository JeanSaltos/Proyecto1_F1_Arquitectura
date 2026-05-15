package ec.edu.espe.taller.service;

import ec.edu.espe.taller.models.OrdenMantenimiento;
import ec.edu.espe.taller.models.VehiculoTaller;

/**
 * Interfaz del servicio de dominio de Taller.
 * Opera exclusivamente con modelos internos (Capa Anticorrupción).
 * NO conoce ni depende de los tipos JAXB generados por el XSD.
 */
public interface ITallerService {

    /**
     * Consulta un vehículo en el repositorio del taller por su matrícula.
     *
     * @param matricula Placa del vehículo
     * @return VehiculoTaller del dominio interno, o null si no existe
     */
    VehiculoTaller consultarVehiculo(String matricula);

    /**
     * Registra una nueva orden de mantenimiento para un vehículo.
     *
     * @param matricula   Placa del vehículo
     * @param descripcion Descripción de la incidencia
     * @return OrdenMantenimiento creada con su ID generado
     */
    OrdenMantenimiento registrarOrden(String matricula, String descripcion);
}
