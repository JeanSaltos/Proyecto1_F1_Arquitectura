package ec.edu.espe.taller.repository;

import ec.edu.espe.taller.models.VehiculoTaller;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TallerRepository {
    private static final Map<String, VehiculoTaller> vehiculos = new HashMap<>();

    @PostConstruct
    public void initData() {
        VehiculoTaller pbc1234 = new VehiculoTaller();
        pbc1234.setMatricula("PBC1234");
        pbc1234.setMarca("Toyota");
        pbc1234.setModelo("Corolla");
        pbc1234.setAnio(2020);
        pbc1234.setEstadoMantenimiento("OPERATIVO");

        vehiculos.put(pbc1234.getMatricula(), pbc1234);

        VehiculoTaller xyz9876 = new VehiculoTaller();
        xyz9876.setMatricula("XYZ9876");
        xyz9876.setMarca("Honda");
        xyz9876.setModelo("Civic");
        xyz9876.setAnio(2022);
        xyz9876.setEstadoMantenimiento("OPERATIVO");

        vehiculos.put(xyz9876.getMatricula(), xyz9876);
    }

    public VehiculoTaller findVehiculo(String matricula) {
        return vehiculos.get(matricula);
    }
    
    public String registrarOrden(String matricula, String descripcion) {
        // En una implementacion real, guardaría la orden en una BD.
        // Aquí solo simulamos que se ha registrado con un UUID.
        System.out.println("Orden registrada para vehículo " + matricula + ": " + descripcion);
        return UUID.randomUUID().toString();
    }
}
