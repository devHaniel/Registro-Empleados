package infrastructure.persistence;

import models.Empleado;
import models.Registro;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmpleadosPersistence {

    private static final String DATA_DIR = "data";

    public List<Empleado> Empleados;

    public EmpleadosPersistence() {
        Empleados = new ArrayList<>();
        cargarDatos();
    }

    private void cargarDatos() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) return;

        for (File file : files) {
            try {
                Empleado empleado = parseFile(file);
                if (empleado != null) {
                    Empleados.add(empleado);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private Empleado parseFile(File file) throws IOException {
        String filename = file.getName();
        String namePart = filename.substring(0, filename.length() - 4);
        int firstDash = namePart.indexOf('-');
        if (firstDash == -1) return null;

        int id = Integer.parseInt(namePart.substring(0, firstDash));
        String nombre = namePart.substring(firstDash + 1);

        Empleado empleado = new Empleado();
        empleado.Id = id;
        empleado.Nombre = nombre;

        List<String> lines = Files.readAllLines(file.toPath());
        if (!lines.isEmpty()) {
            empleado.Salario = Double.parseDouble(lines.get(0));
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            Registro registro = parseRegistro(line);
            if (registro != null) {
                empleado.Registros.add(registro);
            }
        }

        return empleado;
    }

    private Registro parseRegistro(String line) {
        int separador = line.indexOf(" - ");
        Registro r = new Registro();

        if (separador == -1) {
            r.Entrada = LocalDateTime.parse(line);
            return r;
        }

        r.Entrada = LocalDateTime.parse(line.substring(0, separador));

        String salidaStr = line.substring(separador + 3).trim();
        if (!salidaStr.isEmpty()) {
            r.Salida = LocalDateTime.parse(salidaStr);
        }

        return r;
    }

    public void guardarEmpleado(Empleado e) {
        try {
            File dir = new File(DATA_DIR);
            dir.mkdirs();

            Path path = Paths.get(DATA_DIR, e.Id + "-" + e.Nombre + ".txt");
            List<String> lines = new ArrayList<>();

            lines.add(String.valueOf(e.Salario));

            for (Registro r : e.Registros) {
                if (r.Salida != null) {
                    lines.add(r.Entrada.toString() + " - " + r.Salida.toString());
                } else {
                    lines.add(r.Entrada.toString() + " - ");
                }
            }

            Files.write(path, lines);
        } catch (IOException ex) {
            System.err.println("Error al guardar empleado " + e.Id + ": " + ex.getMessage());
        }
    }

    public void eliminarEmpleado(int id) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) return;

        File[] files = dir.listFiles((d, name) -> name.startsWith(id + "-") && name.endsWith(".txt"));
        if (files == null) return;

        for (File file : files) {
            file.delete();
        }
    }
}
