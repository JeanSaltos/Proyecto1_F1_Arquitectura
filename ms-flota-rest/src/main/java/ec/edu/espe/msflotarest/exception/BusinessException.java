package ec.edu.espe.msflotarest.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 * Se mapea automáticamente a un HTTP 409 (Conflict) gracias al GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
