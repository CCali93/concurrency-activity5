/**
 * Created by curtis on 10/5/15.
 */
public class Driver {
    public static void main(String[] args) {
        Banker theBanker = new Banker(123456);

        Client[] theClients = {
            new Client("Billy", theBanker, 34, 10, 0, 20),
            new Client("John Cena", theBanker, 120000, 50, 0, 300),
            new Client("Phillip J. Fry", theBanker, 2500, 75, 100, 500),
            new Client("Bob", theBanker, 1, 150, 10, 100)
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
