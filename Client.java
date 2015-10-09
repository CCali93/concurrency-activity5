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
        long sleepTime, sleepTimeDifference = maxSleepMillis - minSleepMillis;

        banker.setClaim(nUnits);

        for(int i = 0; i < nRequests; i++) {
            if(banker.remaining() == 0) {
                if(banker.allocated() > 0) {
                    banker.release(banker.allocated());
                }
            } else {
                banker.request((int)(banker.remaining() * Math.random()) + 1);
            }

            sleepTime = minSleepMillis + (long)(Math.random() * sleepTimeDifference);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(banker.allocated() > 0) {
            banker.release(banker.allocated());
        }
    }
}