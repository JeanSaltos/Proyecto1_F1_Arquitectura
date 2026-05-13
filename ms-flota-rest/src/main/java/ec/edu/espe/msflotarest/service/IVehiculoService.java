package ec.edu.espe.msflotarest.service;

import ec.edu.espe.msflotarest.dtos.VehiculoDTO;
import ec.edu.espe.msflotarest.models.Vehiculo;

import java.util.List;

public interface IVehiculoService {

    // Recuperar todos los vehículos para el panel de administración
    List<Vehiculo> listarTodos();

    // Buscar un vehículo específico por su ID interno
    Vehiculo buscarPorId(Long id);

    // Buscar por matrícula (útil para validaciones de ruteo)
    Vehiculo buscarPorMatricula(String matricula);

    // Crear un nuevo vehículo validando que la matrícula no exista
    Vehiculo crear(VehiculoDTO vehiculoDTO);

    // Actualizar datos técnicos o estado operativo
    Vehiculo actualizar(Long id, VehiculoDTO vehiculoDTO);

    // Eliminar un vehículo de la flota
    void eliminar(Long id);

    // Cambiar estado (Disponible, Mantenimiento, En Servicio)
    // Este metodo será vital para el módulo de ruteo en el futuro
    Vehiculo actualizarEstado(Long id, String nuevoEstado);
}