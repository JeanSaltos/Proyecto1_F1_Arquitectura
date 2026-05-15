package ec.edu.espe.msflotarest.service;

import ec.edu.espe.msflotarest.dtos.ConductorDTO;
import ec.edu.espe.msflotarest.models.Conductor;

import java.util.List;

public interface IConductorService {

    // Listado de todo el personal operativo
    List<Conductor> listarTodos();

    // Buscar conductor por ID
    Conductor buscarPorId(java.util.UUID id);

    // Buscar por cédula (Validación de identidad)
    Conductor buscarPorCedula(String cedula);

    // Registrar un nuevo conductor en el sistema
    Conductor registrar(ConductorDTO conductorDTO);

    // Actualizar información del conductor (ej. renovación de licencia)
    Conductor actualizar(java.util.UUID id, ConductorDTO conductorDTO);

    // Dar de baja a un conductor
    void eliminar(java.util.UUID id);

    // Consultar conductores disponibles para asignación inmediata
    List<Conductor> listarDisponibles();
}