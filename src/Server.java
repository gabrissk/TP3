import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Server {

    private int port;
    private File ixp;
    private File net;
    private File netixlan;
    private ArrayList<Long> ixps;

    public Server(int port, String ixp, String net, String netixlan) throws IOException, ParseException {
        this.port = port;
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(ixp));
        JSONArray array = (JSONArray) jsonObject.get("data");
        this.ixps = new ArrayList<>();
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            this.ixps.add((Long) data.get("id"));
        }
        System.out.println(this.ixps);

    }

    public static void main(String args[]) throws IOException, ParseException {

        Server server = new Server(Integer.parseInt(args[0]), args[1], args[2], args[3]);
    }

}
