/**
 * Created by curtis on 10/5/15.
 */
public class Driver {
    public static void main(String[] args) {
        Banker theBanker = new Banker(123456);

        Client[] theClients = {
            new Client("Billy", theBanker, 34, 10, 0, 200),
            new Client("John Cena", theBanker, 500, 50, 2000, 3000),
            new Client("Phillip J. Fry", theBanker, 2500, 75, 100, 500),
            new Client("Bob", theBanker, 100000, 150, 1000, 1200)
        };

        for(Client client : theClients) {
            client.start();
        }

        for(Client client : theClients) {
            try {
                client.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
