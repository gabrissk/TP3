/*By: Gabriel Morais
  2018
 */

package redes;
import java.util.ArrayList;
import java.util.List;

public class IXP {

    private int id;
    private String name;
    private int net_count;
    private List<String> nets;

    public IXP(int id, String name) {
        this.id = id;
        this.name = name;
        this.net_count = 0;
        this.nets = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNet_count() {
        return net_count;
    }

    public List<String> getNets() {
        return nets;
    }

    public void setNets(List<String> nets) {
        this.nets = nets;
    }

    public void setNet_count(int net_count) {
        this.net_count = net_count;
    }
}

