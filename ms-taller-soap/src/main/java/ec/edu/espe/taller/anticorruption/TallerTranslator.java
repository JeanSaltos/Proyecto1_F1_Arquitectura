package ec.edu.espe.taller.anticorruption;

import ec.edu.espe.taller.ws.Vehiculo;

/**
 * Traductor de la Capa Anticorrupción (ACL - Anti-Corruption Layer).
 *
 * Esta clase es el componente clave del patrón DDD que protege al dominio interno
 * de Taller de los cambios en el contrato SOAP externo. Traduce bidireccionalmetne
 * entre los modelos internos (VehiculoTaller, OrdenMantenimiento) y los tipos
 * generados por JAXB desde el XSD (ec.edu.espe.taller.ws.*).
 *
 * Si el contrato SOAP cambia (nuevo campo, renombramiento), solo se modifica esta clase.
 * El resto del dominio permanece intacto.
 */
public class TallerTranslator {

    private TallerTranslator() {
        // Utility class — no se instancia
    }

    /**
     * Traduce del modelo JAXB (externo/SOAP) al modelo interno del dominio.
     *
     * @param vehiculoSoap Tipo generado por JAXB desde el XSD
     * @return VehiculoTaller del dominio interno
     */
    public static VehiculoTaller fromSoap(Vehiculo vehiculoSoap) {
        if (vehiculoSoap == null) {
            return null;
        }
        VehiculoTaller interno = new VehiculoTaller();
        interno.setMatricula(vehiculoSoap.getMatricula());
        interno.setMarca(vehiculoSoap.getMarca());
        interno.setModelo(vehiculoSoap.getModelo());
        interno.setAnio(vehiculoSoap.getAnio());
        interno.setEstadoMantenimiento("OPERATIVO"); // Estado por defecto al consultar
        return interno;
    }

    /**
     * Traduce del modelo interno del dominio al tipo JAXB (para respuestas SOAP).
     *
     * @param interno Modelo del dominio de Taller
     * @return Vehiculo tipo JAXB para serialización SOAP
     */
    public static Vehiculo toSoap(VehiculoTaller interno) {
        if (interno == null) {
            return null;
        }
        Vehiculo vehiculoSoap = new Vehiculo();
        vehiculoSoap.setMatricula(interno.getMatricula());
        vehiculoSoap.setMarca(interno.getMarca());
        vehiculoSoap.setModelo(interno.getModelo());
        vehiculoSoap.setAnio(interno.getAnio());
        return vehiculoSoap;
    }

    /**
     * Crea un OrdenMantenimiento del dominio interno a partir de los datos de la petición SOAP.
     *
     * @param matricula   Matrícula del vehículo
     * @param descripcion Descripción de la incidencia
     * @param idOrden     ID generado por el repositorio
     * @return OrdenMantenimiento del dominio interno
     */
    public static OrdenMantenimiento crearOrdenDesdeRequest(String matricula, String descripcion, String idOrden) {
        OrdenMantenimiento orden = new OrdenMantenimiento();
        orden.setIdOrden(idOrden);
        orden.setMatriculaVehiculo(matricula);
        orden.setDescripcion(descripcion);
        orden.setEstado("REGISTRADA");
        orden.setFechaRegistro(java.time.LocalDateTime.now().toString());
        return orden;
    }
}
