import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Genera el archivo de referencias (direcciones virtuales) que produciria
 * la funcion cifrar() del Hill Modificado.
 *
 * Layout en memoria virtual:
 *   - Matriz m (bytes) en row-major desde la direccion 0:
 *       dir(m[i][j]) = i * NC + j
 *   - Vector v (bytes) justo despues de la matriz:
 *       dir(v[k]) = NF * NC + k
 *   - pagina = dir / TP ; desplazamiento = dir % TP
 *
 * Por cada operacion  m[i][j] = m[i][j] OP v[idx]  se generan 3 referencias:
 *   1. lectura de m[i][j]
 *   2. lectura de v[idx]
 *   3. escritura de m[i][j]
 *
 * Uso: java GeneradorReferencias filas columnas tamVector tamPagina pasadas archivoSalida
 * (si no se pasan argumentos, los pide por consola)
 */
public class GeneradorDVS {

    public static void main(String[] args) throws IOException {
        int filas, columnas, tamVector, tamPagina, pasadas;
        String salida;

        if (args.length == 6) {
            filas = Integer.parseInt(args[0]);
            columnas = Integer.parseInt(args[1]);
            tamVector = Integer.parseInt(args[2]);
            tamPagina = Integer.parseInt(args[3]);
            pasadas = Integer.parseInt(args[4]);
            salida = args[5];
        } 
        else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Filas de la matriz: ");
            filas = sc.nextInt();
            System.out.print("Columnas de la matriz: ");
            columnas = sc.nextInt();
            System.out.print("Tamano del vector: ");
            tamVector = sc.nextInt();
            System.out.print("Tamano de pagina (bytes): ");
            tamPagina = sc.nextInt();
            System.out.print("Numero de pasadas: ");
            pasadas = sc.nextInt();
            System.out.print("Nombre del archivo de salida: ");
            salida = sc.next();
        }

        generar(filas, columnas, tamVector, tamPagina, pasadas, salida);
        System.out.println("Archivo generado: " + salida);
    }

    public static void generar(int filas, int columnas, int tamVector, int tamPagina, int pasadas, String salida) throws IOException {

        long tamMatriz = (long) filas * columnas;
        long totalBytes = tamMatriz + tamVector;
        long np = (totalBytes + tamPagina - 1) / tamPagina; //techo
        // Por pasada: recorrido por filas (3*Filas*Columnas) + recorrido por columnas (3*Filas*Columnas)
        long nr = 6L * filas * columnas * pasadas;

        try (BufferedWriter w = new BufferedWriter(new FileWriter(salida), 1 << 16)) {
            // Encabezado (formato del Anexo A)
            w.write("TP=" + tamPagina + "\n");
            w.write("NF1=" + filas + "\n");
            w.write("NC1=" + columnas + "\n");
            w.write("NV=" + tamVector + "\n");
            w.write("numPasadas=" + pasadas + "\n");
            w.write("NR=" + nr + "\n");
            w.write("NP=" + np + "\n");

            for (int pasada = 0; pasada < pasadas; pasada++) {
                // Recorrido por filas (suma)
                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnas; j++) {
                        escribirTriple(w, i, j, j % tamVector, columnas, tamMatriz, tamPagina);
                    }
                }
                // Recorrido por columnas (XOR)
                for (int j = 0; j < columnas; j++) {
                    for (int i = 0; i < filas; i++) {
                        escribirTriple(w, i, j, i % tamVector, columnas, tamMatriz, tamPagina);
                    }
                }
            }
        }
    }

    /* Escribe: lectura m[i][j], lectura v[k], escritura m[i][j]. */ 

    private static void escribirTriple(BufferedWriter w, int i, int j, int k,
                                       int columnas, long tamMatriz, int tamPagina) throws IOException {
        long dirM = (long) i * columnas + j;
        long dirV = tamMatriz + k;

        String refM = "[mat1-" + i + "-" + j + "]," + (dirM / tamPagina) + "," + (dirM % tamPagina) + "\n";
        String refV = "[v-0-" + k + "]," + (dirV / tamPagina) + "," + (dirV % tamPagina) + "\n";

        w.write(refM);
        w.write(refV);
        w.write(refM);
    }
}