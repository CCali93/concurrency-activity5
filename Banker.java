import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Banker.java
 * Representation of a banker that distibutes resources to clients through claims.
 * 
 */
public class Banker {
    private final String ALLOCATED = "allocated";
    private final String REMAINING = "remaining";
    private final String CURRENT   = "current_claim";
    private int nUnits;
    private Map<String, Map<String, Integer>> threadMap = new ConcurrentHashMap<>();

    /**
     * Constructor sets the initial amount of units the banker starts with.
     * @param nUnits - starting units
     */
    public Banker(int nUnits) {
        this.nUnits = nUnits;
    }

    /**
     * The current thread attempts to register a claim for up to nUnits of resource.
     * @param nUnits - units requested for a claim
     */
    public synchronized void setClaim(int nUnits) {
        Thread currentThread = Thread.currentThread();

        if(threadMap.get(currentThread.getName()) != null || nUnits <= 0 || nUnits > this.nUnits) {
            System.out.printf("setClaim Error\n");
            System.exit(1);
        }

        Map<String, Integer> map = new HashMap<>();
        map.put(ALLOCATED, 0);
        map.put(CURRENT, nUnits);
        map.put(REMAINING, nUnits);

        threadMap.put(currentThread.getName(), map);

        System.out.printf("Thread %s sets a claim for %s units.\n", currentThread.getName(), nUnits);

    }

    /**
     * The current thread requests nUnits more resources.
     * @param nUnits - number of resources requested from current claim
     * @return True if request is successful.
     */
    public synchronized boolean request(int nUnits) {
        Thread currentThread = Thread.currentThread();

        if(!threadMap.containsKey(currentThread.getName()) || nUnits <= 0 ||
                nUnits > threadMap.get(currentThread.getName()).get(REMAINING)) {
            System.out.printf("Request Error\n");
            System.exit(1);
        }

        System.out.printf("Thread %s requests %s units.\n", currentThread.getName(), nUnits);

        if(isStateSafe(this.nUnits, Collections.unmodifiableMap(threadMap))) {

            System.out.printf("Thread %s has %s units allocated.\n", currentThread.getName(), nUnits);
            int allocated = threadMap.get(currentThread.getName()).get(ALLOCATED);
            int remaining = threadMap.get(currentThread.getName()).get(REMAINING);
            threadMap.get(currentThread.getName()).put(ALLOCATED, allocated + nUnits);
            threadMap.get(currentThread.getName()).put(REMAINING, remaining - nUnits);

            this.nUnits -= nUnits;

            return true;
        }

        while(!isStateSafe(this.nUnits, Collections.unmodifiableMap(threadMap))) {
            System.out.printf("Thread %s waits.\n", currentThread.getName());
            try {
                wait();
            } catch (InterruptedException ie) {
                System.err.println("Error: " + ie.getMessage() );
            }
        }

        System.out.printf("Thread %s awakened.\n", currentThread.getName());

        return request(nUnits);
    }

    /**
     * The current thread releases nUnits resources.
     * @param nUnits - number of units to release that current threat is using.
     */
    public synchronized void release(int nUnits) {
        Thread currentThread = Thread.currentThread();

        if(!threadMap.containsKey(currentThread.getName()) || nUnits <= 0 ||
                nUnits > threadMap.get(currentThread.getName()).get(ALLOCATED)) {
            System.out.printf("Release Error\n");
            System.exit(1);
        }

        System.out.printf("Thread %s releases %s units.\n", currentThread.getName(), nUnits);

        int allocated = threadMap.get(currentThread.getName()).get(ALLOCATED);
        threadMap.get(currentThread.getName()).put(ALLOCATED, allocated - nUnits);
        
        this.nUnits += nUnits;

        notifyAll();
    }

    /**
     * @return The number of units allocated to the current thread
     */
    public synchronized int allocated() {
        Thread currentThread = Thread.currentThread();
        return threadMap.get(currentThread.getName()).get(ALLOCATED);
    }

    /**
     * @return The number of units remaining in the current thread's claim.
     */
    public synchronized int remaining() {
        Thread currentThread = Thread.currentThread();
        return threadMap.get(currentThread.getName()).get(REMAINING);
    }

    /**
     * 
     * @param numberOfUnitsOnHand - number of units that are available to be lent out
     * @param map - map of threads with the status of their claims
     * @return - true if the state change is safe
     */
    private synchronized boolean isStateSafe(int numberOfUnitsOnHand, Map<String, Map<String, Integer>> map) {
        Map<String, Map<String, Integer>> sortedMap = sortMap(map);

        // This is sorted according to remaining units in ascending order.
        for(Map<String, Integer> threadDetail : sortedMap.values()) {
            if(threadDetail.get(REMAINING) > numberOfUnitsOnHand) {
                return false;
            }

            numberOfUnitsOnHand += threadDetail.get(ALLOCATED);
        }

        return true;
    }

    /**
     * Sorts an unsorted map according to remaining units.
     * @param unsortedMap The map to sort. Must be made final to access it within the inner class.
     * @return A sorted version of this unsortedMap based on remaining units.
     */
    private synchronized Map<String, Map<String, Integer>> sortMap(final Map<String, Map<String, Integer>> unsortedMap){
        Comparator<String> comparator = (obj1, obj2) -> {
            Integer obj1_remaining = unsortedMap.get(obj1).get(REMAINING);
            Integer obj2_remaining = unsortedMap.get(obj2).get(REMAINING);

            return obj1_remaining.compareTo(obj2_remaining) == 0 ? 1 : obj1_remaining.compareTo(obj2_remaining);
        };

        Map<String, Map<String, Integer>> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(unsortedMap);

        return sortedMap;
    }
}
