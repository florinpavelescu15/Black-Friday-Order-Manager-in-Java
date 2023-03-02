import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        // preiau argumentele din linia de comanda
        String folderInput = args[0];
        int MAX_NUMBER_OF_THREADS = Integer.parseInt(args[1]);

        RandomAccessFile raf = new RandomAccessFile(folderInput + "/orders.txt", "r");
        int LENGTH = (int) raf.length();

        // thread-urile de nivel 1
        Thread[] t = new Thread[MAX_NUMBER_OF_THREADS];

        // threadpool cu MAX_NUMBER_OF_THREADS workeri
        ExecutorService tpe = Executors.newFixedThreadPool(MAX_NUMBER_OF_THREADS);

        /*
         * creez fisierele de iesire si le golesc de fiecare data (pentru ca checker-ul
         * nu le sterge :))
         */
        File outFile1 = new File("orders_out.txt");
        outFile1.createNewFile();
        FileWriter writer1 = new FileWriter("orders_out.txt");
        writer1.write("");
        writer1.close();

        File outFile2 = new File("order_products_out.txt");
        outFile2.createNewFile();
        FileWriter writer2 = new FileWriter("order_products_out.txt");
        writer2.write("");
        writer2.close();

        /*
         * impart fisierul orders.txt in mod echitabil thread-urilor de nivel 1 si
         * verific totodata ca fiecare thread sa primeasca un numar "intreg" de linii
         */
        for (int i = 0; i < MAX_NUMBER_OF_THREADS; i++) {
            int start = i * (int) ceil((double) LENGTH / (double) MAX_NUMBER_OF_THREADS);
            int end = min((int) LENGTH, (i + 1) * (int) ceil((double) LENGTH / (double) MAX_NUMBER_OF_THREADS));

            // ASCII('\n') = 10
            if (start > 0) {
                start--;
                raf.seek(start);
                while (raf.readByte() != 10) {
                    start++;
                    raf.seek(start);
                }
                start++;
            }

            end--;
            raf.seek(end);
            while (raf.readByte() != 10) {
                end++;
                raf.seek(end);
            }
            end++;

            t[i] = new Leader(folderInput, start, end, tpe);
            t[i].start();
        }

        for (int i = 0; i < MAX_NUMBER_OF_THREADS; ++i) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // odata ce thread-urile de nivel 1 si-au terminat treaba, inchid threadpool-ul
        tpe.shutdown();
    }
}
