package ec.edu.espe.msflotarest.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en el sistema.
 * Se mapea automáticamente a un HTTP 404 gracias al GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
