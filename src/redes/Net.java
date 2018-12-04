import java.util.ArrayList;

public class Net {

    private Long id;
    private String name;
    private Long ixp_count;
    private ArrayList<Long> ixps;


    public Net(Long id) {
        this.id = id;
        this.ixp_count = Long.valueOf(0);
        this.ixps = new ArrayList<>();
    }

}
