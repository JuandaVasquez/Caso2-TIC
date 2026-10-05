import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GeneradorDVS {

    private static BufferedWriter out;
    private static int tp;
    private static int filas; 
    private static int columnas;
    private static int base;


    public static void main (String[] args){
        if (args.length != 6){
            System.out.println("Uso: filas, columnas, tamVector, tamPagina, numPasadas, archivoSaldia");
            return;
        }

        filas = Integer.parseInt(args[0]);
        columnas = Integer.parseInt(args[1]);
        int nv = Integer.parseInt(args[2]);
        tp = Integer.parseInt(args[3]);
        int numPasadas = Integer.parseInt(args[4]);
        String archivo = args[5];
 
        base = filas * columnas; //direccion donde empieza el vector


    }
    
}
