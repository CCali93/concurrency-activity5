/**
 * Created by curtis on 10/5/15.
 */
public class Banker {
    private int numberOfUnitsOnHand;

    public Banker(int nUnits) {
        numberOfUnitsOnHand = nUnits;
    }

    public void setClaim(int nUnits) {

    }

    public boolean request(int nUnits) {
        return true;
    }

    public void release(int nUnits) {

    }

    public int allocated() {
        return numberOfUnitsOnHand;
    }

    public int remaining() {
        return 0;
    }
}
