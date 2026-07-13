# Historial

Sistema de consola en Java para gestionar empleados y registrar sus entradas y salidas. Los datos se persisten en archivos de texto dentro del directorio `data/`.

## Requisitos

- Java 21+
- Maven

## Compilar

```bash
mvn clean compile
```

## Ejecutar

```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

O directamente:

```bash
mvn package && java -cp target/Historial-1.0-SNAPSHOT.jar org.example.Main
```

## Uso

El menú principal ofrece las siguientes opciones:

1. **Listar empleados** — Muestra todos los empleados registrados.
2. **Buscar empleado** — Busca por ID.
3. **Crear empleado** — Agrega un nuevo empleado con ID, nombre y salario.
4. **Actualizar empleado** — Modifica nombre y/o salario de un empleado existente.
5. **Eliminar empleado** — Elimina un empleado por ID.
6. **Registrar entrada** — Marca la hora de entrada de un empleado.
7. **Registrar salida** — Marca la hora de salida del último registro abierto.
8. **Ver registros** — Muestra el historial de entrada/salida de un empleado.
0. **Salir**

## Persistencia

Cada empleado se guarda en un archivo `data/{id}-{nombre}.txt`. La primera línea es el salario y las siguientes son los registros en formato ISO `LocalDateTime - LocalDateTime`.

## Estructura

```
src/main/java/
├── org/example/Main.java              — Punto de entrada y menú interactivo
├── models/
│   ├── Empleado.java                  — Modelo de empleado
│   └── Registro.java                  — Modelo de registro (entrada/salida)
└── infrastructure/
    ├── EmpleadosRepo.java             — Implementación del repositorio
    ├── interfaces/
    │   └── IEmpleadosRepo.java        — Contrato del repositorio
    └── persistence/
        └── EmpleadosPersistence.java  — Persistencia en archivos
```
