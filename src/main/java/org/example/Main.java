package org.example;

import infrastructure.EmpleadosRepo;
import infrastructure.persistence.EmpleadosPersistence;
import models.Empleado;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        var persistence = new EmpleadosPersistence();
        var repo = new EmpleadosRepo(persistence);
        var scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SISTEMA DE HISTORIAL ===");
            System.out.println("1. Listar empleados");
            System.out.println("2. Buscar empleado");
            System.out.println("3. Crear empleado");
            System.out.println("4. Actualizar empleado");
            System.out.println("5. Eliminar empleado");
            System.out.println("6. Registrar entrada");
            System.out.println("7. Registrar salida");
            System.out.println("8. Ver registros de empleado");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida.");
                continue;
            }

            switch (opcion) {
                case 1 -> listarEmpleados(repo);
                case 2 -> buscarEmpleado(repo, scanner);
                case 3 -> crearEmpleado(repo, scanner);
                case 4 -> actualizarEmpleado(repo, scanner);
                case 5 -> eliminarEmpleado(repo, scanner);
                case 6 -> registrarEntrada(repo, scanner);
                case 7 -> registrarSalida(repo, scanner);
                case 8 -> verRegistros(repo, scanner);
                case 0 -> {
                    System.out.println("Saliendo...");
                    return;
                }
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void listarEmpleados(EmpleadosRepo repo) {
        var empleados = repo.obtenerEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        System.out.println("\n--- EMPLEADOS ---");
        for (var e : empleados) {
            System.out.printf("%d | %s | Salario: %.2f | Registros: %d%n",
                    e.Id, e.Nombre, e.Salario, e.Registros.size());
        }
    }

    private static void buscarEmpleado(EmpleadosRepo repo, Scanner scanner) {
        int id = leerId(scanner, "ID del empleado: ");
        if (id < 0) return;
        var emp = repo.obtenerEmpleado(id);
        if (emp == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }
        System.out.printf("ID: %d | Nombre: %s | Salario: %.2f | Registros: %d%n",
                emp.Id, emp.Nombre, emp.Salario, emp.Registros.size());
    }

    private static void crearEmpleado(EmpleadosRepo repo, Scanner scanner) {
        System.out.println("\n--- NUEVO EMPLEADO ---");
        int id = leerId(scanner, "ID: ");
        if (id < 0) return;

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacio.");
            return;
        }

        double salario = leerSalario(scanner);

        var emp = new Empleado();
        emp.Id = id;
        emp.Nombre = nombre;
        emp.Salario = salario;

        if (repo.crearEmpleado(emp)) {
            System.out.println("Empleado creado correctamente.");
        } else {
            System.out.println("Ya existe un empleado con ese ID.");
        }
    }

    private static void actualizarEmpleado(EmpleadosRepo repo, Scanner scanner) {
        int id = leerId(scanner, "ID del empleado a actualizar: ");
        if (id < 0) return;

        var existente = repo.obtenerEmpleado(id);
        if (existente == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.println("Deja en blanco para mantener el valor actual.");
        System.out.printf("Nombre actual: %s%n", existente.Nombre);
        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.printf("Salario actual: %.2f%n", existente.Salario);
        double salario = leerSalario(scanner);

        var datos = new Empleado();
        datos.Id = id;
        datos.Nombre = nombre;
        datos.Salario = salario;

        if (repo.actualizarEmpleado(id, datos)) {
            System.out.println("Empleado actualizado correctamente.");
        } else {
            System.out.println("No se pudo actualizar.");
        }
    }

    private static void eliminarEmpleado(EmpleadosRepo repo, Scanner scanner) {
        int id = leerId(scanner, "ID del empleado a eliminar: ");
        if (id < 0) return;

        if (repo.eliminarEmpleado(id)) {
            System.out.println("Empleado eliminado correctamente.");
        } else {
            System.out.println("Empleado no encontrado.");
        }
    }

    private static void registrarEntrada(EmpleadosRepo repo, Scanner scanner) {
        int id = leerId(scanner, "ID del empleado: ");
        if (id < 0) return;
        repo.registrarEntrada(id);
        var emp = repo.obtenerEmpleado(id);
        if (emp != null) {
            var ultimo = emp.Registros.get(emp.Registros.size() - 1);
            System.out.println("Entrada registrada: " + ultimo.Entrada.format(FMT));
        }
    }

    private static void registrarSalida(EmpleadosRepo repo, Scanner scanner) {
        int id = leerId(scanner, "ID del empleado: ");
        if (id < 0) return;
        repo.registrarSalida(id);
    }

    private static void verRegistros(EmpleadosRepo repo, Scanner scanner) {
        int id = leerId(scanner, "ID del empleado: ");
        if (id < 0) return;

        var emp = repo.obtenerEmpleado(id);
        if (emp == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.printf("Registros de %s (ID %d):%n", emp.Nombre, emp.Id);
        if (emp.Registros.isEmpty()) {
            System.out.println("Sin registros.");
            return;
        }

        for (int i = 0; i < emp.Registros.size(); i++) {
            var r = emp.Registros.get(i);
            String entrada = r.Entrada.format(FMT);
            String salida = r.Salida != null ? r.Salida.format(FMT) : "---";
            System.out.printf("  %d. Entrada: %s | Salida: %s%n", i + 1, entrada, salida);
        }
    }

    private static int leerId(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
            return -1;
        }
    }

    private static double leerSalario(Scanner scanner) {
        while (true) {
            System.out.print("Salario: ");
            try {
                double s = Double.parseDouble(scanner.nextLine().trim());
                if (s < 0) {
                    System.out.println("El salario no puede ser negativo.");
                    continue;
                }
                return s;
            } catch (NumberFormatException e) {
                System.out.println("Salario invalido.");
            }
        }
    }
}
