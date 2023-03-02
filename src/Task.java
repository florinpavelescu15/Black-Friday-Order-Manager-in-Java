import java.io.*;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

public class Task extends Thread {
    private String folderInput;
    private String orderID;
    private int productIndex;
    private Semaphore sem;

    public Task(String folderInput, String orderID, int productIndex, Semaphore sem) {
        this.folderInput = folderInput;
        this.orderID = orderID;
        this.productIndex = productIndex;
        this.sem = sem;
    }

    @Override
    public void run() {
        /*
         * citesc fisierul prder_products.txt, linie cu linie, pana gasesc al
         * productIndex-lea produs din comanda cu id-ul orderID
         */
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(folderInput + "/order_products.txt"));
            String line = reader.readLine();
            int idx = 0;

            while (line != null) {
                String currentOrderID;
                String currentProductID;

                // extrag id-ul comenzii si id-ul produsului de pe linia curenta
                StringTokenizer buff = new StringTokenizer(line, ",\n");
                currentOrderID = buff.nextToken();
                currentProductID = buff.nextToken();

                /*
                 * cand gasesc produsul cautat, il scriu in order_products_out.txt, folosind un
                 * bloc synchronized pentru a evita situatia in care mai multe thread-uri scriu
                 * simultan si termin citirea din fisier
                 */
                if (currentOrderID.equals(orderID)) {
                    if (idx == productIndex) {
                        synchronized (Leader.class) {
                            FileWriter writer = new FileWriter("order_products_out.txt", true);
                            writer.append(line + ",shipped\n");
                            writer.close();
                            sem.release();
                        }
                        break;
                    } else {
                        idx++;
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
