package infrastructure;

import infrastructure.interfaces.IEmpleadosRepo;
import infrastructure.persistence.EmpleadosPersistence;
import models.Empleado;
import models.Registro;

import java.time.LocalDateTime;
import java.util.List;

public class EmpleadosRepo implements IEmpleadosRepo {

    private EmpleadosPersistence empPersistence;

    public EmpleadosRepo(EmpleadosPersistence empleadosPersistence)
    {
        empPersistence = empleadosPersistence;
    }

    @Override
    public List<Empleado> obtenerEmpleados() {
        return empPersistence.Empleados;
    }

    @Override
    public Empleado obtenerEmpleado(int id) {
        var empleado = empPersistence.Empleados.stream()
                .filter(e -> e.Id == id)
                .findFirst()
                .orElse(null);

        return empleado;
    }

    @Override
    public boolean crearEmpleado(Empleado entidad) {
        if (obtenerEmpleado(entidad.Id) != null)
            return false;

        empPersistence.Empleados.add(entidad);
        empPersistence.guardarEmpleado(entidad);
        return true;
    }

    @Override
    public boolean actualizarEmpleado(int id, Empleado entidad) {
        var empleado = obtenerEmpleado(id);
        if (empleado == null)
            return false;

        boolean nombreCambio = !entidad.Nombre.isEmpty() && !entidad.Nombre.equals(empleado.Nombre);

        if (nombreCambio) {
            empPersistence.eliminarEmpleado(id);
            empleado.Nombre = entidad.Nombre;
        } else if (!entidad.Nombre.isEmpty()) {
            empleado.Nombre = entidad.Nombre;
        }

        if (entidad.Salario > 0)
            empleado.Salario = entidad.Salario;

        empPersistence.guardarEmpleado(empleado);
        return true;
    }

    @Override
    public boolean eliminarEmpleado(int id) {
        boolean removed = empPersistence.Empleados.removeIf(e -> e.Id == id);
        if (removed) {
            empPersistence.eliminarEmpleado(id);
        }
        return removed;
    }

    @Override
    public void registrarEntrada(int id) {
        var empleado = obtenerEmpleado(id);
        if (empleado == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        var registro = new Registro();
        registro.Entrada = LocalDateTime.now();

        empleado.Registros.add(registro);
        empPersistence.guardarEmpleado(empleado);
    }

    @Override
    public void registrarSalida(int id) {
        var empleado = obtenerEmpleado(id);
        if (empleado == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        var registros = empleado.Registros;
        if (registros.isEmpty()) {
            System.out.println("No existen registros.");
            return;
        }

        var ultimo = registros.get(registros.size() - 1);
        if (ultimo.Salida != null) {
            System.out.println("Ya se registró la salida.");
            return;
        }

        ultimo.Salida = LocalDateTime.now();
        empPersistence.guardarEmpleado(empleado);
    }
}
