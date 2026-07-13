package infrastructure.interfaces;

import models.Empleado;

import java.util.List;

public interface IEmpleadosRepo  {

    public List<Empleado> obtenerEmpleados();
    public Empleado obtenerEmpleado(int id);
    public boolean crearEmpleado(Empleado entidad);
    public boolean actualizarEmpleado(int id, Empleado entidad);
    public boolean eliminarEmpleado(int id);

    public void registrarEntrada(int id);
    public void registrarSalida(int id);
}
