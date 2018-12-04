import java.util.ArrayList;

public class IXP {

    private Long id;
    private String name;
    private Long net_count;
    private ArrayList<Long> nets;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getNet_count() {
        return net_count;
    }

    public ArrayList<Long> getNets() {
        return nets;
    }

    public IXP(Long id, String name) {
        this.id = id;
        this.name = name;
        this.net_count = Long.valueOf(0);
        this.nets = new ArrayList<>();
    }
}

