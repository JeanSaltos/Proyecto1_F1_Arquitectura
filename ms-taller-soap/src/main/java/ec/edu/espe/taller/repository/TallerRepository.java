package ec.edu.espe.taller.repository;

import ec.edu.espe.taller.ws.Vehiculo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TallerRepository {
    private static final Map<String, Vehiculo> vehiculos = new HashMap<>();

    @PostConstruct
    public void initData() {
        Vehiculo pbc1234 = new Vehiculo();
        pbc1234.setMatricula("PBC1234");
        pbc1234.setMarca("Toyota");
        pbc1234.setModelo("Corolla");
        pbc1234.setAnio(2020);

        vehiculos.put(pbc1234.getMatricula(), pbc1234);

        Vehiculo xyz9876 = new Vehiculo();
        xyz9876.setMatricula("XYZ9876");
        xyz9876.setMarca("Honda");
        xyz9876.setModelo("Civic");
        xyz9876.setAnio(2022);

        vehiculos.put(xyz9876.getMatricula(), xyz9876);
    }

    public Vehiculo findVehiculo(String matricula) {
        return vehiculos.get(matricula);
    }
    
    public String registrarOrden(String matricula, String descripcion) {
        // En una implementacion real, guardaría la orden en una BD.
        // Aquí solo simulamos que se ha registrado.
        System.out.println("Orden registrada para vehículo " + matricula + ": " + descripcion);
        return "ORD-" + System.currentTimeMillis();
    }
}
