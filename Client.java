/**
 * Created by curtis on 10/5/15.
 */
public class Client extends Thread {
    private Banker banker;

    private int nUnits, nRequests;
    private long minSleepMillis, maxSleepMillis;

    public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis) {
        super(name);

        this.banker = banker;

        this.nUnits = nUnits;
        this.nRequests = nRequests;

        this.minSleepMillis = minSleepMillis;
        this.maxSleepMillis = maxSleepMillis;
    }

    public void run() {
        int allocatedToSelf = 0;
        long sleepTime, sleepTimeDifference = maxSleepMillis - minSleepMillis;

        for(int i = 0; i < nRequests; i++) {
            if(banker.remaining() == 0) {
                banker.release(allocatedToSelf);
            } else {
                banker.request(this.nUnits);
                allocatedToSelf = banker.allocated();
            }

            sleepTime = minSleepMillis + (long)(Math.random() * sleepTimeDifference);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        banker.release(allocatedToSelf);
    }
}