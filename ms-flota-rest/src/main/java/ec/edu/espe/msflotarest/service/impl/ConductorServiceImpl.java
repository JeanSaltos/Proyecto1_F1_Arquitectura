package ec.edu.espe.msflotarest.service.impl;

import ec.edu.espe.msflotarest.dtos.ConductorDTO;
import ec.edu.espe.msflotarest.exception.BusinessException;
import ec.edu.espe.msflotarest.exception.ResourceNotFoundException;
import ec.edu.espe.msflotarest.models.Conductor;
import ec.edu.espe.msflotarest.repositories.ConductorRepository;
import ec.edu.espe.msflotarest.service.IConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConductorServiceImpl implements IConductorService {

    @Autowired
    private ConductorRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Conductor> listarTodos() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Conductor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Conductor buscarPorCedula(String cedula) {
        return repository.findByCedula(cedula)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un conductor con la cédula: " + cedula));
    }

    @Override
    @Transactional
    public Conductor registrar(ConductorDTO dto) {
        if (repository.existsByCedula(dto.getCedula())) {
            throw new BusinessException("La cédula " + dto.getCedula() + " ya se encuentra registrada en el sistema");
        }

        Conductor conductor = new Conductor();
        mapearDtoAEntidad(dto, conductor);
        return repository.save(conductor);
    }

    @Override
    @Transactional
    public Conductor actualizar(Long id, ConductorDTO dto) {
        Conductor conductorExistente = buscarPorId(id);

        // Validar si la cédula cambió y si la nueva ya existe
        if (!conductorExistente.getCedula().equals(dto.getCedula()) &&
                repository.existsByCedula(dto.getCedula())) {
            throw new BusinessException("La nueva cédula ya pertenece a otro conductor registrado");
        }

        mapearDtoAEntidad(dto, conductorExistente);
        return repository.save(conductorExistente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Error al eliminar: Conductor con ID " + id + " no encontrado");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conductor> listarDisponibles() {
        // Filtra conductores que están aptos para asignación inmediata
        return repository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getDisponible()))
                .collect(Collectors.toList());
    }

    /**
     * Mapeo manual de DTO a Entidad para mantener el control de los datos.
     */
    private void mapearDtoAEntidad(ConductorDTO dto, Conductor entidad) {
        entidad.setCedula(dto.getCedula());
        entidad.setNombre(dto.getNombre());
        entidad.setLicencia(dto.getLicencia());
        entidad.setDisponible(dto.getDisponible());
    }
}