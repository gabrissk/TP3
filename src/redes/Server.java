import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        System.out.println(jsonObject.toJSONString());
        System.out.println(array.toJSONString());
        System.out.println(array.toString());

    }

    public static void main(String args[]) throws IOException, ParseException {

        Server server = new Server(Integer.parseInt(args[0]), args[1], args[2], args[3]);
        ServerSocket s = new ServerSocket(server.port);
        Socket socket = s.accept();

        BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        String clientSentence = inFromClient.readLine();
        System.out.println("Received: " + clientSentence);

    }

}
