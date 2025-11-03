package com.nicolasMorales;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        int [] OPERACIONES_BASICAS_MERGE_SORT = new int[1];
       List<String[]> datos = leerArchivo();
       final int INDEX_PRECIO = 4;
       final int INDEX_TIPO = 5;
       //Filtramos los datos para trabajar solo con los descuentos por cada litro
        List<String[]> filtrado = datos.stream()
                .filter(fila ->  fila[INDEX_TIPO].toLowerCase().trim().contains("de euro en cada litro"))
                .collect(Collectors.toList());
        System.out.println("-----------------------------------------------------");
        List<String[]> seleccion = algoritmoSeleccion(INDEX_PRECIO, filtrado);
        seleccion.forEach(r -> System.out.println(String.join("|", r)));
        System.out.println("-----------------------------------------------------");
        List<String[]> burbuja = algoritmoBurbuja(INDEX_PRECIO, filtrado);
        burbuja.forEach(r -> System.out.println(String.join("|", r)));
        System.out.println("-----------------------------------------------------");
        List<String[]> insercion = algoritmoInsercion(INDEX_PRECIO, filtrado);
        insercion.forEach(r -> System.out.println(String.join("|", r)));
        System.out.println("-----------------------------------------------------");
        long TIEMPO_INICIO = System.nanoTime();
        List<String[]> mergeSort = algoritmoMergeSort(INDEX_PRECIO, filtrado, OPERACIONES_BASICAS_MERGE_SORT);
        long TIEMPO_FIN = System.nanoTime();
        mergeSort.forEach(r -> System.out.println(String.join("|", r)));
        System.out.println(String.format("Tiempo de ejecución ALGORITMO DE MERGE SORT: %s", (TIEMPO_FIN - TIEMPO_INICIO) / 1_000_000.0));
        System.out.println(String.format("Número de ejecucion de operaciones basicas: %s", (Object) Arrays.stream(OPERACIONES_BASICAS_MERGE_SORT).findFirst().getAsInt()));
        System.out.println("-----------------------------------------------------");
        List<String[]> quickSort = algoritmoQuickSort(INDEX_PRECIO, filtrado);
        quickSort.forEach(r -> System.out.println(String.join("|", r)));
    }

    /**
     * Función para leer el csv
     * @return datos del csv
     */
    public static List<String[]> leerArchivo() {
        List<String[]> data = new ArrayList<>();
        try (InputStream is = Main.class.getClassLoader()
                .getResourceAsStream("planesDescuento.csv")) {
            if (is == null) {
                System.err.println("Error: Archivo planesDescuento.csv no encontrado como recurso.");
                return data;
            }
            try (CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String[] encabezados = reader.readNext();
                if (encabezados == null) {
                    System.err.println("Error: Archivo CSV vacío.");
                    return data;
                }
                String[] nextRecord;
                while ((nextRecord = reader.readNext()) != null) {
                    data.add(nextRecord);
                }
            }
            return data;
        } catch (Exception e) {
            System.err.println("Error al leer el archivo con OpenCSV: " + e.getMessage());
            e.printStackTrace();
            return data;
        }
    }

    /**
     * En cada iteración compara si el precio del indíce actual es menor al del indíce anterior.
     * Sí se cumple esta condición intercambia los registros y los precios mas bajos iran al principio del array,
     * mientras que los mas altos al final del array.
     *
     * @param columnIndex
     * @param data
     * @return Lista de datos ordenados por precio
     *
     */
    public static List<String[]> algoritmoBurbuja(int columnIndex, List<String[]> data) {
        long TIEMPO_INICIO = System.currentTimeMillis();
        int OPERACIONES_BASICAS = 0;
        int n = data.size();
        for (int i = 0; i < n - 1; i++) {
            OPERACIONES_BASICAS += 1;
            for (int j = 0; j < n - i - 1; j++) {
                OPERACIONES_BASICAS += 3; // INCREMENTO POR LA COMPARACION DEL FOR Y EL ACCESO A DATOS
                String[] filaActual = data.get(j);
                String[] filaSiguiente = data.get(j + 1);
                if (filaActual.length <= columnIndex || filaSiguiente.length <= columnIndex) {
                    OPERACIONES_BASICAS += 2; // INCREMENTO POR LAS COMPARACIONES DEL IF
                    continue;
                }
                try {
                    String precioStr1 = filaActual[columnIndex].trim().replace(",", ".");
                    String precioStr2 = filaSiguiente[columnIndex].trim().replace(",", ".");
                    double precio1 = precioStr1.isBlank() ? 0.0 : Double.parseDouble(precioStr1);
                    double precio2 = precioStr2.isBlank() ? 0.0 : Double.parseDouble(precioStr2);
                    if (precio1 > precio2) { // Aqui se produce el swap
                        OPERACIONES_BASICAS += 3; // 3 INCREMENTOS POR LA COMPARACION DEL IF Y LOS SWAP
                        data.set(j, filaSiguiente);
                        data.set(j + 1, filaActual);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al parsear número en fila " + j + ": " + e.getMessage());
                }
            }
        }
        long TIEMPO_FIN = System.nanoTime();
        System.out.println(String.format("Tiempo de ejecución ALGORITMO BURBUJA: %s", (TIEMPO_FIN - TIEMPO_INICIO) / 1_000_000.0));
        System.out.println(String.format("Número de ejecucion de operaciones basicas: %s", OPERACIONES_BASICAS));
        return data;
    }

    /**
     * Busca y selecciona el menor elemento del array y lo coloca en el iníce minimo.
     *
     * @param columnIndex
     * @param data
     * @return Lista de datos ordenados por precio
     *
     */
    public static List<String[]> algoritmoSeleccion(int columnIndex, List<String[]> data) {
        long TIEMPO_INICIO = System.nanoTime();
        int OPERACIONES_BASICAS = 0;
        int n = data.size();
        for (int i = 0; i < n - 1; i++) {
            OPERACIONES_BASICAS += 1; // INCREMENTOS POR LAS COMPARACIONES DEL FOR
            int indiceMinimo = i;
            for (int j = i + 1; j < n; j++) {
                OPERACIONES_BASICAS += 3; // INCREMENTOS POR LAS COMPARACIONES DEL FOR Y ACCESO A DATOS
                String[] filaMinima = data.get(indiceMinimo);
                String[] filaActual = data.get(j);
                if (filaMinima.length <= columnIndex || filaActual.length <= columnIndex) {
                    OPERACIONES_BASICAS += 2; // INCREMENTOS POR LAS COMPARACIONES DEL IF
                    continue;
                }
                try {
                    String valorMinStr = filaMinima[columnIndex].trim().replace(",", ".");
                    String valorActStr = filaActual[columnIndex].trim().replace(",", ".");
                    double valorMin = valorMinStr.isBlank() ? 0.0 : Double.parseDouble(valorMinStr);
                    double valorAct = valorActStr.isBlank() ? 0.0 : Double.parseDouble(valorActStr);
                    if (valorAct < valorMin) {
                        OPERACIONES_BASICAS += 2; // incremento por el if y las asignacion
                        indiceMinimo = j;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al parcear el numero de la fila:" + j);
                }
            }
            if (indiceMinimo != i) {
                OPERACIONES_BASICAS += 5; // incremento por el if, las asignaciones Y ACCESO A DATOS
                String[] temp = data.get(i);
                data.set(i, data.get(indiceMinimo));
                data.set(indiceMinimo, temp);
            }
        }
        long TIEMPO_FIN = System.nanoTime();
        System.out.println(String.format("Tiempo de ejecución ALGORITMO DE SELECCIÓN: %s", (TIEMPO_FIN - TIEMPO_INICIO) / 1_000_000.0));
        System.out.println(String.format("Número de ejecucion de operaciones basicas: %s", OPERACIONES_BASICAS));
        return data;
    }
    /**
     * Divide en dos el array, una parte con elementos ordenados y otra con elementos no ordenados
     * En cada iteracción mueve los elmentos del desordenado al ordenado (que estan al principio).
     *
     * @param columnIndex
     * @param data
     * @return Lista de datos ordenados por precio
     *
     */
    public static List<String[]> algoritmoInsercion(int columnIndex, List<String[]> data) {
        long TIEMPO_INICIO = System.nanoTime();
        int OPERACIONES_BASICAS = 0;
        int n = data.size();
        for (int i = 1; i < n; i++) {
            OPERACIONES_BASICAS += 3; // incremento por asignacion, comparacion del for y asignacion de variable
            String[] filaActual = data.get(i);
            int j = i - 1;
            double valorActual = 0.0;
            try {
                if (filaActual.length > columnIndex) {
                    OPERACIONES_BASICAS += 1; // incremento por comparacion
                    String valorStr = filaActual[columnIndex].trim().replace(",", ".");
                    valorActual = valorStr.isBlank() ? 0.0 : Double.parseDouble(valorStr);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error, valor no numerico en fila " + i);
            }
            while (j >= 0) {
                OPERACIONES_BASICAS += 2; // incremento por asignacion y comparacion del bucle
                String[] filaComparada = data.get(j);
                if (filaComparada.length <= columnIndex) {
                    OPERACIONES_BASICAS += 2; // incremento por asignaciones y comparacion del if
                    j--;
                    continue;
                }
                double valorComparado = 0.0;
                try {
                    String valorStr = filaComparada[columnIndex].trim().replace(",", ".");
                    valorComparado = valorStr.isBlank() ? 0.0 : Double.parseDouble(valorStr);
                } catch (NumberFormatException e) {
                    System.err.println("Error, valor no numerico en fila " + j);
                }
                if (valorComparado > valorActual) {
                    OPERACIONES_BASICAS += 3; // incremento por incremento de variable, seteo y comparacion del if
                    data.set(j + 1, filaComparada);
                    j--;
                } else {
                    break;
                }
            }
            OPERACIONES_BASICAS += 2; // incremento por incremento de variable y seteo
            data.set(j + 1, filaActual);
        }
        long TIEMPO_FIN = System.nanoTime();
        System.out.println(String.format("Tiempo de ejecución ALGORITMO DE INSERCIÓN: %s", (TIEMPO_FIN - TIEMPO_INICIO) / 1_000_000.0));
        System.out.println(String.format("Número de ejecucion de operaciones basicas: %s", OPERACIONES_BASICAS));
        return data;
    }

    // Algoritmos Merge

    /**
     * Divide el array por la mitad, luego ordena cada subarray y une ambos arrays.
     * Utiliza la estrategia divide y conquista de manera recursiva.
     *
     * @param columnIndex
     * @param data
     * @param OPERACIONES_BASICAS_MERGE_SORT
     * @return Lista de datos ordenados por precio
     */
    public static List<String[]> algoritmoMergeSort(int columnIndex, List<String[]> data, int[] OPERACIONES_BASICAS_MERGE_SORT) {
        if (data.size() <= 1) {
            OPERACIONES_BASICAS_MERGE_SORT[0] += 1; // INCREMENTO POR COMPARACIÓN DEL IF
            return data;
        }
        int mitad = data.size() / 2;
        OPERACIONES_BASICAS_MERGE_SORT [0] += 2; // INCREMENTO POR ASIGNACION Y ACCESO A DATOS
        List<String[]> izquierda = new ArrayList<>(data.subList(0, mitad));
        List<String[]> derecha = new ArrayList<>(data.subList(mitad, data.size()));
        izquierda = algoritmoMergeSort(columnIndex, izquierda, OPERACIONES_BASICAS_MERGE_SORT);
        derecha = algoritmoMergeSort(columnIndex, derecha, OPERACIONES_BASICAS_MERGE_SORT);
        return merge(columnIndex, izquierda, derecha, OPERACIONES_BASICAS_MERGE_SORT );
    }
    public static List<String[]> merge(int columnIndex, List<String[]> izquierda, List<String[]> derecha, int[] OPERACIONES_BASICAS) {
        List<String[]> resultado = new ArrayList<>();
        int i = 0, j = 0;
        while (i < izquierda.size() && j < derecha.size()) {
            OPERACIONES_BASICAS[0] += 2; // INCREMENTO POR COMPARACIONES DEL BUCLE
            double valorIzq = obtenerValorNumerico(izquierda.get(i), columnIndex);
            double valorDer = obtenerValorNumerico(derecha.get(j), columnIndex);
            if (valorIzq <= valorDer) {
                OPERACIONES_BASICAS[0] += 4; // INCREMENTO POR COMPARACIÓN DEL IF, OBTENCIÓN DE DATOS Y INCREMENTO DE VARIABLE Y SETEO DE DATOS
                resultado.add(izquierda.get(i));
                i++;
            } else {
                OPERACIONES_BASICAS[0] += 3;  // INCREMENTO POR OBTENCIÓN DE DATOS Y INCREMENTO DE VARIABLE Y SETEO DE DATOS
                resultado.add(derecha.get(j));
                j++;
            }
        }
        while (i < izquierda.size()) {
            OPERACIONES_BASICAS[0] += 4; // INCREMENTO POR COMPARACIONES DEL BUCLE, SETEO E INCREMENTO DE VARIABLE
            resultado.add(izquierda.get(i++));
        }
        while (j < derecha.size()) {
            OPERACIONES_BASICAS[0] += 4; // INCREMENTO POR COMPARACIONES DEL BUCLE, SETEO E INCREMENTO DE VARIABLE
            resultado.add(derecha.get(j++));
        }
        return resultado;
    }
    public static double obtenerValorNumerico(String[] fila, int columnIndex) {
        if (fila == null || fila.length <= columnIndex) return 0.0;
        try {
            String valorStr = fila[columnIndex].trim().replace(",", ".");
            return valorStr.isBlank() ? 0.0 : Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            System.err.println("Error, valor no numérico " + Arrays.toString(fila));
            return 0.0;
        }
    }
    /**
     * Tambien usa la estrategia divide y conquista,
     * No divide el array por el medio.
     * A diferencia del anterior, este no usa un array temporal.
     *
     * @param columnIndex
     * @param data
     * @return Lista de datos ordenados por precio
     *
     */
    public static List<String[]> algoritmoQuickSort(int columnIndex, List<String[]> data) {
        long TIEMPO_INICIO = System.nanoTime();
        int[] OPERACIONES_BASICAS = new int[1];
        if (data == null || data.size() <= 1) {
            OPERACIONES_BASICAS[0] += 2; // incremento por comparaciones del if
            return data;
        }
        quickSortRecursivo(data, 0, data.size() - 1, columnIndex, OPERACIONES_BASICAS);
        long TIEMPO_FIN = System.nanoTime();
        System.out.println(String.format("Tiempo de ejecución ALGORITMO QUICK SORT: %s", (TIEMPO_FIN - TIEMPO_INICIO) / 1_000_000.0));
        System.out.println(String.format("Número de ejecucion de operaciones basicas: %s", Arrays.stream(OPERACIONES_BASICAS).findFirst().getAsInt()));
        return data;
    }
    private static void quickSortRecursivo(List<String[]> data, int inicio, int fin, int columnIndex, int[] operaciones) {
        int[] contador = operaciones;
        if (inicio < fin) {
            contador[0] += 1; // incremento por comparaciones del if
            int indiceParticion = particionar(data, inicio, fin, columnIndex, operaciones);
            quickSortRecursivo(data, inicio, indiceParticion - 1, columnIndex, operaciones);
            quickSortRecursivo(data, indiceParticion + 1, fin, columnIndex, operaciones);
        }
    }
    private static int particionar(List<String[]> data, int inicio, int fin, int columnIndex, int[] operaciones) {
        double pivote = obtenerValorNumerico(data.get(fin), columnIndex);
        int i = inicio - 1;
        for (int j = inicio; j < fin; j++) {
            double valorActual = obtenerValorNumerico(data.get(j), columnIndex);
            if (valorActual <= pivote) {
                operaciones[0] += 6; // incremento por comparacion del if, seteo y asignaciones
                i++;
                String[] temp = data.get(i);
                data.set(i, data.get(j));
                data.set(j, temp);
            }
        }
        operaciones[0] += 7; // incremento por incremento de variable, seteos y obtencion de datos
        String[] temp = data.get(i + 1);
        data.set(i + 1, data.get(fin));
        data.set(fin, temp);
        return i + 1;
    }
}