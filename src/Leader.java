import java.io.*;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class Leader extends Thread {
    private String folderInput;
    private int start;
    private int end;
    private ExecutorService tpe;

    public Leader(String folderInput, int start, int end, ExecutorService tpe) {
        this.folderInput = folderInput;
        this.start = start;
        this.end = end;
        this.tpe = tpe;
    }

    @Override
    public void run() {
        try {
            RandomAccessFile raf = new RandomAccessFile(folderInput + "/orders.txt", "r");
            int size = end - start;

            // citesc liniile repartizate thread-ului curent din orders.txt
            if (size > 0) {
                byte[] bytes = new byte[size];
                raf.seek(start);
                raf.read(bytes, 0, size);
                raf.close();

                // extrag comenzile
                String orderID;
                int productsNumber;
                StringTokenizer buff = new StringTokenizer(new String(bytes), ",\n");

                while (buff.hasMoreTokens()) {
                    orderID = buff.nextToken();
                    productsNumber = Integer.parseInt(buff.nextToken());

                    // initializez un semafor
                    Semaphore sem = new Semaphore(-productsNumber + 1);

                    // pentru fiecare produs din comanda, adaug un thread de nivel 2 in threadpool
                    for (int i = 0; i < productsNumber; i++) {
                        tpe.submit(new Task(folderInput, orderID, i, sem));
                    }

                    /*
                     * inainte de a scrie in orders_out.txt si a trece la comanda urmatoare, astept
                     * ca toate produsele din comanda curenta sa fie procesate
                     */
                    sem.acquire();

                    /*
                     * scriu in orders_out.txt comanda curenta, folosind un bloc synchronized pentru
                     * a evita situatia in care mai multe thread-uri scriu simultan
                     */
                    if (productsNumber > 0) {
                        synchronized (Leader.class) {
                            FileWriter writer = new FileWriter("orders_out.txt", true);
                            writer.append(orderID + "," + productsNumber + ",shipped\n");
                            writer.close();
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
