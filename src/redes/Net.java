import java.util.ArrayList;

public class Net {

    private int id;
    private String name;
    private int ixp_count;
    private ArrayList<Integer> ixps;


    public Net(int id) {
        this.id = id;
        this.ixp_count = 0;
        this.ixps = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIxp_count() {
        return ixp_count;
    }

    public ArrayList<Integer> getIxps() {
        return ixps;
    }

    public void setIxp_count(int ixp_count) {
        this.ixp_count = ixp_count;
    }

    public void setIxps(ArrayList<Integer> ixps) {
        this.ixps = ixps;
    }
}
