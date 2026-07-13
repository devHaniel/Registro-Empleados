package models;

import java.util.ArrayList;
import java.util.List;

public class Empleado {
    public int Id;
    public String Nombre;
    public double Salario;

    public List<Registro> Registros = new ArrayList<>();

}
