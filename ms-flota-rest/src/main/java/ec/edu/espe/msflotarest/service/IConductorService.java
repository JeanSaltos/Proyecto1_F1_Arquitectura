package ec.edu.espe.msflotarest.service;

import ec.edu.espe.msflotarest.dtos.ConductorDTO;
import ec.edu.espe.msflotarest.models.Conductor;

import java.util.List;

public interface IConductorService {

    // Listado de todo el personal operativo
    List<Conductor> listarTodos();

    // Buscar conductor por ID
    Conductor buscarPorId(Long id);

    // Buscar por cédula (Validación de identidad)
    Conductor buscarPorCedula(String cedula);

    // Registrar un nuevo conductor en el sistema
    Conductor registrar(ConductorDTO conductorDTO);

    // Actualizar información del conductor (ej. renovación de licencia)
    Conductor actualizar(Long id, ConductorDTO conductorDTO);

    // Dar de baja a un conductor
    void eliminar(Long id);

    // Consultar conductores disponibles para asignación inmediata
    List<Conductor> listarDisponibles();
}