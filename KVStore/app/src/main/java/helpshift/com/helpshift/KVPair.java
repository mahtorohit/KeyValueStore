package helpshift.com.helpshift;

/**
 * Created by Rohit on 08/05/16.
 */
public class KVPair {
    String k;

    public long getLk() {
        return lk;
    }

    public void setLk(long lk) {
        this.lk = lk;
    }

    long lk;
    String v; // We can extend the scope from just String to more data types like int , long or data by putting this as Object and saving as BLOB in sqllite

    public KVPair(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public KVPair(String v, long lk) {
        this.lk = lk;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}
